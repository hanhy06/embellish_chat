package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class AdminCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                        commandDispatcher.register(
                                CommandManager.literal("embellish-chat")
                                        .then(CommandManager.literal("reload")
                                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                                .executes(AdminCommand::executeReloadConfig))
                                        .then(CommandManager.literal("ban")
                                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                                        .executes(context -> executeBanOrPardon(context, true))))
                                        .then(CommandManager.literal("pardon")
                                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                                        .executes(context -> executeBanOrPardon(context, false))))
                                        .then(CommandManager.literal("stress_test")
                                                .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                                                .then(CommandManager.argument("time", IntegerArgumentType.integer())
                                                        .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                                                .then(CommandManager.argument("text", StringArgumentType.string())
                                                                        .executes(AdminCommand::executeStressTest))))
                                        )
                        )
        );
    }

    private static int executeReloadConfig(CommandContext<ServerCommandSource> context) {
        Text feedback;
        if (ConfigManager.INSTANCE.readConfig()){
            feedback = Text.literal("Config reloaded successfully.");
        }else {
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
            EmbellishChat.LOGGER.error("Unable to perform {} due to an unknown error.",action);
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
        int time = IntegerArgumentType.getInteger(context, "time");
        int count = IntegerArgumentType.getInteger(context, "count");
        String text  = StringArgumentType.getString(context,"text");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendFeedback(() -> Text.literal("Player only command."),false);
            return 0;
        }

        if (count > 5000) {
            source.sendFeedback(() -> Text.literal("Count is too large. (Max 5000)"), true);
            return 0;
        }

        stressTest(time, count, text, player.getUuid(), source.getServer()).thenAccept(result -> {
            source.sendFeedback(() -> Text.literal("Stress test completed!"), true);

            long total = result.stream().mapToLong(Long::longValue).sum();
            long min = result.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = result.stream().mapToLong(Long::longValue).max().orElse(0);
            double average = total / (double) result.size();

            result.sort(null);
            double median;
            int size = result.size();
            if (size % 2 == 0) {
                median = (result.get(size / 2 - 1) + result.get(size / 2)) / 2.0;
            } else {
                median = result.get(size / 2);
            }

            String resultMessage = """
                    <yellow>Results</yellow>
                    • <aqua>Iterations</aqua>: %d
                    • <green>Average</green>: %.2fms
                    • <dark_green>Minimum</dark_green>: %dms
                    • <red>Maximum</red>: %dms
                    • <light_purple>Median</light_purple>: %.2fms
                    • <gold>Total</gold>: %dms
                    """;

            source.sendFeedback(() -> PlaceHolderUtil.parseTag(String.format(
                    resultMessage, size, average, min, max, median, total
            )), true);
        });

        source.sendFeedback(() -> Text.literal("Starting stress test..."), true);
        return 1;
    }

    private static CompletableFuture<List<Long>> stressTest(int time, int count,String text, UUID uuid, MinecraftServer server) {
        CompletableFuture<List<Long>> future = new CompletableFuture<>();

        server.submit(() -> {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                SignedMessage message = SignedMessage.ofUnsigned(uuid, text);
                MessageProcessor.INSTANCE.handleMessage(message);
            }
            long duration = System.currentTimeMillis() - startTime;

            if (time > 1) {
                stressTest(time-1,count,text,uuid,server).thenAccept(list -> {
                    list.addFirst(duration);
                    future.complete(list);
                });
            } else {
                List<Long> result = new ArrayList<>();
                result.add(duration);
                future.complete(result);
            }
        });

        return future;
    }
}