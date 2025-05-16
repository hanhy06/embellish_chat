package com.hanhy06.betterchat.gui.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

public class InboxCommand {
    public static void  registerInboxCommand(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                    commandDispatcher
                            .register(CommandManager
                                    .literal("inbox")
                                    .executes(InboxCommand::openGui)
                            )
                );
    }

    private static int openGui(CommandContext<ServerCommandSource> context){
        context.getSource().getPlayer().sendMessage(Text.literal("test"));
        return 1;
    }
}
