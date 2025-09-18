package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EmbellishChatCommand {
    public static void registerBetterChatCommand(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("embellish_chat")
                                    .then(
                                            CommandManager.literal("reload")
                                                    .executes(EmbellishChatCommand::executeReloadConfig)
                                    )
                    );
                }
        );
    }

    private static int executeReloadConfig(CommandContext<ServerCommandSource> context) {
        ConfigManager.INSTANCE.readConfig();
        ConfigManager.INSTANCE.broadcastConfig();
        context.getSource().sendFeedback(()-> Text.literal("embellish chat mod config loaded"),true);
        return 1;
    }
}
