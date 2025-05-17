package com.hanhy06.betterchat.gui.command;

import com.hanhy06.betterchat.gui.InboxGui;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InboxCommand {
    private final InboxGui inboxGui;

    public InboxCommand(InboxGui inboxGui) {
        this.inboxGui = inboxGui;
    }

    public void  registerInboxCommand(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                    commandDispatcher
                            .register(CommandManager
                                    .literal("inbox")
                                    .executes(commandContext -> openGui(commandContext))
                            )
                );
    }

    private int openGui(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (syncId, playerInventory, player1) ->
                                new GenericContainerScreenHandler(
                                        ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inboxGui.createInboxGui(player.getGameProfile(), 0), 6
                                ),
                        Text.literal("inbox")
                )
        );
        return 1;
    }
}
