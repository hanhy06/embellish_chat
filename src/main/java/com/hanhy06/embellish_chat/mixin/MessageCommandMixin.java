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

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @WrapOperation(
            method = "execute(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/Collection;Lnet/minecraft/network/message/SignedMessage;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/message/SentMessage;of(Lnet/minecraft/network/message/SignedMessage;)Lnet/minecraft/network/message/SentMessage;"
            )
    )
    private static SentMessage execute(
            SignedMessage msg,
            Operation<SentMessage> originalCall,
            ServerCommandSource source,
            Collection<ServerPlayerEntity> targets
    ) {
        return originalCall.call(ChatHandler.INSTANCE.handleChatMessage(
                msg
        ));
    }
}