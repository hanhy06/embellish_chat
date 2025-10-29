package com.hanhy06.embellish_chat.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class EcCommand {
    public static void registerEc(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("ec")
                                    .then(
                                            CommandManager.literal("help")
                                                    .executes(EcCommand::executeHelp)
                                    )

                    );
                }
        );
    }

    private static int executeHelp(CommandContext<ServerCommandSource> context){
        return 1;
    }
}
