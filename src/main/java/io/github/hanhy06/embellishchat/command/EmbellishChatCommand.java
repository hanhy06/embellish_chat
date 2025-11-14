package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class EmbellishChatCommand {
    public static void registerEmbellishChat() {
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                        commandDispatcher.register(
                                CommandManager.literal("embellish-chat")
                                        .requires(src -> src.hasPermissionLevel(2))
                                        .then(CommandManager.literal("reload")
                                                .executes(EmbellishChatCommand::executeReloadConfig))
                                        .then(CommandManager.literal("ban")
                                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                                        .executes(context -> executeBanOrPardon(context, true))))
                                        .then(CommandManager.literal("pardon")
                                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                                        .executes(context -> executeBanOrPardon(context, false))))
                                        .then(CommandManager.literal("test")
                                                .then(CommandManager.literal("regex")
                                                        .then(CommandManager.argument("regex", StringArgumentType.string())
                                                                .then(CommandManager.argument("test", StringArgumentType.string())
                                                                        .executes(EmbellishChatCommand::executeRegexTest))))
                                                .then(CommandManager.literal("stress")
                                                        .then(CommandManager.argument("count", IntegerArgumentType.integer())
                                                                .then(CommandManager.argument("test", StringArgumentType.string())
                                                                        .executes(EmbellishChatCommand::executeStressTest))))
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

    private static int executeBanOrPardon(CommandContext<ServerCommandSource> context, boolean ban) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");

        if (ban) {
            ConfigManager.getConfig().bannedPlayerList().addAll(
                    players.stream().map(ServerPlayerEntity::getUuid).toList()
            );
        } else {
            ConfigManager.getConfig().bannedPlayerList().removeAll(
                    players.stream().map(ServerPlayerEntity::getUuid).toList()
            );
        }

        ConfigManager.INSTANCE.writeConfig();

        String playerNames = players.stream()
                .map(ServerPlayerEntity::getName)
                .map(Text::getString)
                .collect(Collectors.joining(", "));
        String action = ban ? "banned" : "pardoned";
        String result = String.format("Player(s) %s %s.", playerNames, action);

        context.getSource().sendFeedback(() -> Text.literal(result), true);
        return 1;
    }

    private static int executeRegexTest(CommandContext<ServerCommandSource> context) {
        String regexString = StringArgumentType.getString(context, "regex");
        String testString = StringArgumentType.getString(context, "test");

        Pattern pattern;
        Matcher matcher;

        try {
            pattern = Pattern.compile(regexString);
            matcher = pattern.matcher(testString);
        } catch (PatternSyntaxException e) {
            String error = String.format("Failed to compile regex: \"%s\" (%s)", regexString, e.getMessage());
            context.getSource().sendFeedback(() -> Text.literal(error), false);
            return 0;
        }

        if (!matcher.find()) {
            context.getSource().sendFeedback(() -> Text.literal("No match found."), false);
            return 0;
        }

        String result = formatMatchResult(matcher);
        context.getSource().sendFeedback(() -> Text.literal(result), false);
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

        if (count > 500) {
            source.sendFeedback(() -> Text.literal("Count is too large. (Max 500)"), true);
            return 0;
        }

        SignedMessage testMessage = SignedMessage.ofUnsigned(
                player.getUuid(),
                test
        );

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            MessageProcessor.INSTANCE.handleMessage(testMessage);
        }
        long duration = System.currentTimeMillis() - startTime;

        double messagesPerSecond = (count * 1000.0) / duration;
        String result = String.format("Stress test complete: Processed %d messages in %dms (%.2f msg/s)", count, duration, messagesPerSecond);
        source.sendFeedback(() ->
                        Text.literal(result),
                false
        );
        return 1;
    }
}