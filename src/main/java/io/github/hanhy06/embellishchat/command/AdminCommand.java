package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

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
                                                .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                                        .then(CommandManager.argument("test", StringArgumentType.string())
                                                                .executes(AdminCommand::executeStressTest))))
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

    private static String formatMatchResult(Matcher matcher) {
        int groupCount = matcher.groupCount();

        if (groupCount == 1) {
            return String.format("Matched (Group 1): %s", matcher.group(1));
        } else if (groupCount == 2) {
            return String.format("Matched [Group 1: %s] | [Group 2: %s]", matcher.group(1), matcher.group(2));
        } else {
            return "Invalid regex or test string. (Requires 1 or 2 capture groups)";
        }
    }

    private static int executeStressTest(CommandContext<ServerCommandSource> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        String test  = StringArgumentType.getString(context,"test");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            source.sendFeedback(() ->
                            Text.literal("Stress test must be run by a player in-game."),
                    false
            );
            return 0;
        }

        if (count > 5000) {
            source.sendFeedback(() -> Text.literal("Count is too large. (Max 5000)"), true);
            return 0;
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            SignedMessage testMessage = SignedMessage.ofUnsigned(
                    player.getUuid(),
                    test
            );

            MessageProcessor.INSTANCE.handleMessage(testMessage);
        }
        long duration = System.currentTimeMillis() - startTime;

        double messagesPerSecond = (count * 1000.0) / duration;
        String result = String.format("Stress test complete: Processed %d messages in %dms (%.2f msg/s)", count, duration, messagesPerSecond);
        source.sendFeedback(() ->
                        Text.literal(result),
                false
        );
        return (int) duration;
    }
}