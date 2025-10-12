package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.stream.Collectors;

public class EmbellishChatCommand {
    public static void registerBetterChatCommand(){
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
                                                                    .executes(EmbellishChatCommand::executeBanPlayer)
                                                    )
                                    )
                                    .then(
                                            CommandManager.literal("pardon")
                                                    .then(
                                                            CommandManager.argument("target", EntityArgumentType.players())
                                                                    .executes(EmbellishChatCommand::executePardonPlayer)
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

    private static int executeBanPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players;

        players = EntityArgumentType.getPlayers(context,"target");

        ConfigManager.getConfig().bannedPlayerList().addAll(
                players.stream().map(ServerPlayerEntity::getUuid).toList()
        );
        ConfigManager.INSTANCE.writeConfig();
        context.getSource().sendFeedback(
                () -> Text.literal(
                        String.format(
                                "Player(s) %s has been banned.",
                                players.stream().map(ServerPlayerEntity::getName).map(Text::getString).collect(Collectors.joining(", "))
                        )
                ),
                true
        );

        return 1;
    }

    private static int executePardonPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players;

        players = EntityArgumentType.getPlayers(context,"target");

        ConfigManager.getConfig().bannedPlayerList().removeAll(
                players.stream().map(ServerPlayerEntity::getUuid).toList()
        );
        ConfigManager.INSTANCE.writeConfig();
        context.getSource().sendFeedback(
                () -> Text.literal(
                        String.format(
                                "Player(s) %s has been pardoned.",
                                players.stream().map(ServerPlayerEntity::getName).map(Text::getString).collect(Collectors.joining(", "))
                        )
                ),
                true
        );

        return 1;
    }
}
