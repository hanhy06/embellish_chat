package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.message.MessageProcessor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
    public static void registerEmbellishChat(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("embellish_chat")
                                    .requires(src -> src.hasPermissionLevel(2))
                                    .then(
                                            CommandManager.literal("reload")
                                                    .executes(EmbellishChatCommand::executeReloadConfig)
                                    )
                                    .then(
                                            CommandManager.literal("ban")
                                                    .then(
                                                            CommandManager.argument("target", EntityArgumentType.players())
                                                                    .executes(context -> executeBanOrPardon(context,true))
                                                    )
                                    )
                                    .then(
                                            CommandManager.literal("pardon")
                                                    .then(
                                                            CommandManager.argument("target", EntityArgumentType.players())
                                                                    .executes(context -> executeBanOrPardon(context,false))
                                                    )
                                    )
                                    .then(
                                            CommandManager.literal("test")
                                                    .then(
                                                            CommandManager.literal("regex").then(
                                                                    CommandManager.argument("regex", StringArgumentType.string())
                                                                            .then(
                                                                                    CommandManager.argument("test",StringArgumentType.string())
                                                                                            .executes(EmbellishChatCommand::executeRegexTest)
                                                                            )
                                                            )
                                                    )
                                                    .then(
                                                            CommandManager.literal("stress").then(
                                                                    CommandManager.argument("count", IntegerArgumentType.integer())
                                                                            .executes(EmbellishChatCommand::executeStressTest)
                                                            )
                                                    )
                                    )
                    );
                }
        );
    }

    private static int executeReloadConfig(CommandContext<ServerCommandSource> context) {
        ConfigManager.INSTANCE.readConfig();
        context.getSource().sendFeedback(()-> Text.literal("embellish chat mod config loaded"),true);
        return 1;
    }

    private static int executeBanOrPardon(CommandContext<ServerCommandSource> context, boolean determine) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context,"target");

        if (determine){
            ConfigManager.getConfig().bannedPlayerList().addAll(
                    players.stream().map(ServerPlayerEntity::getUuid).toList()
            );
        }else {
            ConfigManager.getConfig().bannedPlayerList().removeAll(
                    players.stream().map(ServerPlayerEntity::getUuid).toList()
            );
        }

        ConfigManager.INSTANCE.writeConfig();

        String result = String.format(
                "Player(s) %s has been %s.",
                players.stream().map(ServerPlayerEntity::getName).map(Text::getString).collect(Collectors.joining(", ")),
                determine ? "banned" : "pardoned"
        );
        context.getSource().sendFeedback(() -> Text.literal(result),true);

        return 1;
    }

    private static int executeRegexTest(CommandContext<ServerCommandSource> context){
        Pattern pattern;
        Matcher matcher;
        String result;

        try {
            pattern = Pattern.compile(StringArgumentType.getString(context,"regex"));
            matcher = pattern.matcher(StringArgumentType.getString(context,"test"));
        }catch (PatternSyntaxException e){
            String error = String.format("Failed to compile pattern pattern: \"%s\" (%s)",StringArgumentType.getString(context,"regex"),e.getMessage());
            context.getSource().sendFeedback(() -> Text.literal(error),false);
            return 0;
        }

        if (!matcher.find()) {
            context.getSource().sendFeedback(() -> Text.literal("no match"), false);
            return 0;
        }

        if (matcher.groupCount() == 1){
            result = String.format("matched group1: %s",matcher.group(1));
        }else if (matcher.groupCount() == 2){;
            result = String.format("matched group1: %s | matched group2: %s",matcher.group(1),matcher.group(2));
        }else {
            result = "The entered regular expression or test string is invalid.";
        }

        context.getSource().sendFeedback(() -> Text.literal(result),false);
        return 1;
    }

    private static int executeStressTest(CommandContext<ServerCommandSource> context){
        int count = IntegerArgumentType.getInteger(context, "count");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            source.sendFeedback(() -> Text.literal("The stress test of Embellish Chat requires the user's UUID for more accurate testing, please run it in-game instead of from the console."),false);
            return 0;
        }
        if (count >= 400) {
            source.sendFeedback(() -> Text.literal("400"), true);
            return 0;
        }

        SignedMessage testMessage = SignedMessage.ofUnsigned(
                player.getUuid(),
                "@everyone @here **Check out this new [update]<green> __news__** right [here](https://github.com/hanhy06/embellish_chat)! _First come, first served — join now for an exclusive ||special|| gift!_ ~~If you come late, there won't be any left~~"
        );
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            MessageProcessor.INSTANCE.handleMessage(testMessage);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        String result = String.format(
                "Stress test completed: %d messages processed in %dms (%.2f msg/s)",
                count, duration, (count * 1000.0) / duration
        );
        source.sendFeedback(() -> Text.literal(result), false);
        return 1;
    }
}
