package io.github.hanhy06.embellishchat.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdminCommand {
    private static final String STRESS_TEST_FORMAT = """
        <gray>── Performance Analysis ──</gray>
        • <aqua>Ticks</aqua>: %d
        • <dark_green>Minimum</dark_green>: %dms
        • <red>Maximum</red>: %dms
        • <green>Average</green>: %.2fms
        • <light_purple>Median</light_purple>: %.2fms
        • <gold>Total Processing Time</gold>: %dms

        <gray>── Tick Analysis ──</gray>
        • <blue>Used Ticks</blue>: %d / %d
        • <blue>Usage</blue>: %.2f%%
        """;

    private static int remainingTicks = 0;
    private static int countPerTick = 0;
    private static String testText = "";
    private static UUID testUuid = null;
    private static ServerCommandSource testSource = null;
    private static List<Long> testResults = new ArrayList<>();

    public static void registerCommand(String command) {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                CommandManager.literal(command)
                        .then(CommandManager.literal("reload")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .executes(AdminCommand::executeReloadConfig)
                        )
                        .then(CommandManager.literal("ban")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                        .executes(context -> executeBanOrPardon(context, true))
                                )
                        )
                        .then(CommandManager.literal("pardon")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                        .executes(context -> executeBanOrPardon(context, false))
                                )
                        )
                        .then(CommandManager.literal("stress_test")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .then(CommandManager.argument("ticks", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                                .then(CommandManager.argument("text", StringArgumentType.string())
                                                        .executes(AdminCommand::executeStressTest)))
                                )
                                .then(CommandManager.literal("stop")
                                        .executes(source -> {
                                            if (testSource == null || remainingTicks <= 0){
                                                source.getSource().sendError(Text.literal("Stress test is not running."));
                                            }else {
                                                completeStressTest();
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(CommandManager.literal("regex_test")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .then(CommandManager.argument("regex",StringArgumentType.string())
                                        .then(CommandManager.argument("text",StringArgumentType.string())
                                                .executes(AdminCommand::executeRegexTest))
                                )
                        )
                        .then(CommandManager.literal("banlist")
                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                .executes(AdminCommand::executeBanlist)
                        )
                )
        );

        ServerTickEvents.START_SERVER_TICK.register(AdminCommand::onServerTick);
    }

    private static int executeReloadConfig(CommandContext<ServerCommandSource> context) {
        Text feedback;
        if (ConfigManager.INSTANCE.readConfig()) {
            feedback = Text.literal("Config reloaded successfully.");
        } else {
            feedback = Text.literal("Failed to reload config. Please check the log.");
        }
        context.getSource().sendFeedback(() -> feedback, true);
        return 1;
    }

    private static int executeBanOrPardon(CommandContext<ServerCommandSource> context, boolean isBan) {
        String action = isBan ? "banned" : "pardoned";
        Set<UUID> uuids;
        String names;

        try {
            names = EntityArgumentType.getPlayers(context, "target").stream()
                    .map(ServerPlayerEntity::getName)
                    .map(Text::getString)
                    .collect(Collectors.joining(", "));
            uuids = EntityArgumentType.getPlayers(context, "target").stream()
                    .map(ServerPlayerEntity::getUuid)
                    .collect(Collectors.toSet());
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.error("Unable to perform {} due to an unknown error.", action);
            return 1;
        }

        synchronized (ConfigManager.INSTANCE.LOCK_KEY) {
            if (isBan) ConfigManager.getConfig().bannedPlayerList().addAll(uuids);
            else ConfigManager.getConfig().bannedPlayerList().removeAll(uuids);
        }
        CompletableFuture.runAsync(() -> {
            try {
                ConfigManager.INSTANCE.writeConfig();
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to save config async", e);
            }
        });

        String result = String.format("Player(s) %s %s.", names, action);

        context.getSource().sendFeedback(() -> Text.literal(result), true);
        return 1;
    }

    private static int executeStressTest(CommandContext<ServerCommandSource> context) {
        if (remainingTicks > 0) {
            context.getSource().sendFeedback(() -> Text.literal("Stress test is already running."), false);
            return 0;
        }

        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        int count = IntegerArgumentType.getInteger(context, "count");
        String text = StringArgumentType.getString(context, "text");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendFeedback(() -> Text.literal("Player only command."), false);
            return 0;
        }

        if (count > 5000) {
            source.sendFeedback(() -> Text.literal("Count is too large. (Max 5000)"), false);
            return 0;
        }

        remainingTicks = ticks;
        countPerTick = count;
        testText = text;
        testUuid = player.getUuid();
        testSource = source;
        testResults = new ArrayList<>();

        source.sendFeedback(() -> Text.literal("Starting stress test..."), true);
        return 1;
    }

    private static void onServerTick(MinecraftServer server) {
        if (remainingTicks <= 0) {
            return;
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < countPerTick; i++) {
            SignedMessage message = SignedMessage.ofUnsigned(testUuid, testText);
            MessageProcessor.INSTANCE.handleMessage(message);
        }
        testResults.add((System.nanoTime() - startTime) / 1_000_000L);
        remainingTicks--;

        if (remainingTicks <= 0) {
            completeStressTest();
        }
    }

    private static void completeStressTest() {
        if (remainingTicks <= 0){
            return;
        }

        int totalTicks = testResults.size();
        long totalProcessing = testResults.stream().mapToLong(Long::longValue).sum();
        long min = testResults.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = testResults.stream().mapToLong(Long::longValue).max().orElse(0);
        double average = totalProcessing / (double) totalTicks;

        testResults.sort(null);
        double median;
        if (totalTicks % 2 == 0) {
            median = (testResults.get(totalTicks / 2 - 1) + testResults.get(totalTicks / 2)) / 2.0;
        } else {
            median = testResults.get(totalTicks / 2);
        }

        long occupiedTicks = totalProcessing / 50;
        double usagePercentTicks = (occupiedTicks / (double) totalTicks) * 100.0;

        Text message = PlaceHolderUtil.parseTag(String.format(
                STRESS_TEST_FORMAT,
                totalTicks, min, max, average, median, totalProcessing,
                occupiedTicks, totalTicks, usagePercentTicks
        ));

        testSource.sendFeedback(() -> Text.literal("Stress test completed!"), true);
        testSource.sendFeedback(() -> message, true);
        EmbellishChat.LOGGER.info(message.getString());

        testSource = null;
        testUuid = null;
        testResults = new ArrayList<>();
    }

    private static int executeRegexTest(CommandContext<ServerCommandSource> context){
        String text = StringArgumentType.getString(context,"text");
        String regex = StringArgumentType.getString(context,"regex");
        MutableText result = Text.empty();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;
        while (matcher.find()){
            result.append(text.substring(lastEnd,matcher.start(1)));
            result.append(Text.literal(matcher.group(1)).setStyle(Style.EMPTY.withColor(0xaaffaa)));
            result.append(text.substring(matcher.end(1),matcher.start(2)));
            result.append(Text.literal(matcher.group(2)).setStyle(Style.EMPTY.withColor(0xffaaaa)));
            lastEnd = matcher.end(2);
        }
        result.append(text.substring(lastEnd));

        context.getSource().sendFeedback(() -> result,false);
        return 1;
    }

    private static int executeBanlist(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        HashSet<UUID> bannedList = ConfigManager.getConfig().bannedPlayerList();

        source.sendMessage(PlaceHolderUtil.parseTag(
                "<gray>-----</gray> <aqua><b>Banned Player List</b></aqua> <gray>-----</gray>"
        ));

        GameProfileResolver resolver = EmbellishChat.SERVER.getApiServices().profileResolver();
        for (UUID uuid : bannedList) {
            Optional<GameProfile> profile = resolver.getProfileById(uuid);
            if (profile.isEmpty()) continue;
            String message = "<red><b>name</b></red>: " + profile.get().name();
            source.sendMessage(PlaceHolderUtil.parseTag(message));
        }

        source.sendMessage(PlaceHolderUtil.parseTag("<gray>------------------------------</gray>"));
        return 1;
    }

}
