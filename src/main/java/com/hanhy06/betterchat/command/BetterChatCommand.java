package com.hanhy06.betterchat.command;

import com.hanhy06.betterchat.config.ConfigManager;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class BetterChatCommand {
    public static void registerBetterChatCommand(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("betterchat")
                                    .then(
                                            CommandManager.literal("reload")
                                                    .executes(BetterChatCommand::reloadConfigData)
                                    )
                    );
                }
        );
    }

    private static int reloadConfigData(CommandContext<ServerCommandSource> context) {
        ConfigManager.loadConfig();
        context.getSource().getPlayer().sendMessage(Text.literal("better chat mod config loaded"));
        return 1;
    }
}
