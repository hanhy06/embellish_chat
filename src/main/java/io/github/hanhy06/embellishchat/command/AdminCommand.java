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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ProfileResolver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class AdminCommand {
    private static final String STRESS_TEST_FORMAT = """
        <gray>── Test Configuration ──</gray>
        • <yellow>Total Ticks</yellow>: %d
        • <yellow>Count Per Tick</yellow>: %d
        • <yellow>Test Message</yellow>: %s
    
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
    private static CommandSourceStack testSource = null;
    private static List<Long> testResults = null;

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                Commands.literal(EmbellishChat.MOD_ID)
                        .then(Commands.literal("reload")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(AdminCommand::executeReloadConfig)
                        )
                        .then(Commands.literal("ban")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes(context -> executeBanOrPardon(context, true))
                                )
                        )
                        .then(Commands.literal("pardon")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes(context -> executeBanOrPardon(context, false))
                                )
                        )
                        .then(Commands.literal("stress_test")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                        .then(Commands.argument("count", IntegerArgumentType.integer())
                                                .then(Commands.argument("text", StringArgumentType.string())
                                                        .executes(AdminCommand::executeStressTest)))
                                )
                                .then(Commands.literal("stop")
                                        .executes(source -> {
                                            if (testSource == null || remainingTicks <= 0){
                                                source.getSource().sendFailure(Component.literal("Stress test is not running."));
                                            }else {
                                                completeStressTest();
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("regex_test")
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("regex",StringArgumentType.string())
                                        .then(Commands.argument("text",StringArgumentType.string())
                                                .executes(AdminCommand::executeRegexTest))
                                )
                        )
//                        .then(CommandManager.literal("banlist")
//                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
//                                .executes(AdminCommand::executeBanlist)
//                        )
                )
        );

        ServerTickEvents.START_SERVER_TICK.register(AdminCommand::onServerTick);
    }

    private static int executeReloadConfig(CommandContext<CommandSourceStack> context) {
        Component feedback;
        if (ConfigManager.INSTANCE.readConfig()) {
            feedback = Component.literal("Config reloaded successfully.");
        } else {
            feedback = Component.literal("Failed to reload config. Please check the log.");
        }
        context.getSource().sendSuccess(() -> feedback, true);
        return 1;
    }

    private static int executeBanOrPardon(CommandContext<CommandSourceStack> context, boolean isBan) {
        String action = isBan ? "banned" : "pardoned";
        Set<UUID> uuids;
        String names;

        try {
            names = EntityArgument.getPlayers(context, "target").stream()
                    .map(ServerPlayer::getName)
                    .map(Component::getString)
                    .collect(Collectors.joining(", "));
            uuids = EntityArgument.getPlayers(context, "target").stream()
                    .map(ServerPlayer::getUUID)
                    .collect(Collectors.toSet());
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.error("Unable to perform {} due to an unknown error.", action);
            return 0;
        }

        synchronized (ConfigManager.INSTANCE.LOCK_KEY) {
            if (isBan) ConfigManager.getConfig().banned_players().addAll(uuids);
            else ConfigManager.getConfig().banned_players().removeAll(uuids);
        }
        CompletableFuture.runAsync(() -> {
            try {
                ConfigManager.INSTANCE.writeConfig();
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to save config async", e);
            }
        });

        String result = String.format("Player(s) %s %s.", names, action);

        context.getSource().sendSuccess(() -> Component.literal(result), true);
        return 1;
    }

    private static int executeStressTest(CommandContext<CommandSourceStack> context) {
        if (remainingTicks > 0) {
            context.getSource().sendSuccess(() -> Component.literal("Stress test is already running."), false);
            return 0;
        }

        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        int count = IntegerArgumentType.getInteger(context, "count");
        String text = StringArgumentType.getString(context, "text");
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendSuccess(() -> Component.literal("Player only command."), false);
            return 0;
        }

        if (count > 5000) {
            source.sendSuccess(() -> Component.literal("Count is too large. (Max 5000)"), false);
            return 0;
        }

        remainingTicks = ticks;
        countPerTick = count;
        testText = text;
        testUuid = player.getUUID();
        testSource = source;
        testResults = new ArrayList<>();

        source.sendSuccess(() -> Component.literal("Starting stress test..."), true);
        return 1;
    }

    private static void onServerTick(MinecraftServer server) {
        if (remainingTicks <= 0) {
            return;
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < countPerTick; i++) {
            PlayerChatMessage message = PlayerChatMessage.unsigned(testUuid, testText);
            MessageProcessor.INSTANCE.handleMessage(message);
        }
        testResults.add((System.nanoTime() - startTime) / 1_000_000L);
        testSource.getPlayer().displayClientMessage(Component.literal("Time remaining: %d tick".formatted(remainingTicks)),true);
        remainingTicks--;

        if (remainingTicks <= 0) {
            completeStressTest();
        }
    }

    private static void completeStressTest() {
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

        Component message = PlaceHolderUtil.parseTag(String.format(
                STRESS_TEST_FORMAT,
                totalTicks+remainingTicks,countPerTick,testText,
                totalTicks, min, max, average, median, totalProcessing,
                occupiedTicks, totalTicks, usagePercentTicks
        ));

        testSource.sendSuccess(() -> Component.literal("Stress test completed!"), true);
        testSource.sendSuccess(() -> message, true);
        EmbellishChat.LOGGER.info(message.getString());

        testSource = null;
        testUuid = null;
        testResults = null;
        remainingTicks = 0;
    }

    private static int executeRegexTest(CommandContext<CommandSourceStack> context){
        String text = StringArgumentType.getString(context,"text");
        String regex = StringArgumentType.getString(context,"regex");
        MutableComponent result = Component.empty();

        Matcher matcher;

        try {
            matcher = Pattern.compile(regex).matcher(text);
        }catch (PatternSyntaxException e){
            context.getSource().sendFailure(Component.literal("syntax error"));
            return 0;
        }

        if (matcher.groupCount() < 2) {
            context.getSource().sendSuccess(() -> Component.literal("Two capture groups are required."), false);
            return 0;
        }

        int lastEnd = 0;
        while (matcher.find()){
            result.append(text.substring(lastEnd,matcher.start(1)));
            result.append(Component.literal(matcher.group(1)).setStyle(Style.EMPTY.withColor(0xaaffaa)));
            result.append(text.substring(matcher.end(1),matcher.start(2)));
            result.append(Component.literal(matcher.group(2)).setStyle(Style.EMPTY.withColor(0xffaaaa)));
            lastEnd = matcher.end(2);
        }
        result.append(text.substring(lastEnd));

        context.getSource().sendSuccess(() -> result,false);
        return 1;
    }

    private static int executeBanlist(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        HashSet<UUID> bannedList = ConfigManager.getConfig().banned_players();

        source.sendSystemMessage(PlaceHolderUtil.parseTag(
                "<gray>-----</gray> <aqua><b>Banned Player List</b></aqua> <gray>-----</gray>"
        ));

        ProfileResolver resolver = EmbellishChat.SERVER.services().profileResolver();
        for (UUID uuid : bannedList) {
            Optional<GameProfile> profile = resolver.fetchById(uuid);
            if (profile.isEmpty()) continue;
            String message = "<red><b>name</b></red>: " + profile.get().name();
            source.sendSystemMessage(PlaceHolderUtil.parseTag(message));
        }

        source.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>------------------------------</gray>"));
        return 1;
    }
}
