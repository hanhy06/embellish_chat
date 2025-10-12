package com.hanhy06.embellish_chat.mixin;

import com.hanhy06.embellish_chat.chat.ChatHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @ModifyVariable(
            method = "execute",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 2
    )
    private static SignedMessage tweakMessageAtHead(SignedMessage original) {
        return ChatHandler.INSTANCE.handleChatMessage(original);
    }
}
