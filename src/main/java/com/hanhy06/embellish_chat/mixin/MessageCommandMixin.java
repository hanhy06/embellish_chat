package com.hanhy06.embellish_chat.mixin;

import com.hanhy06.embellish_chat.chat.ChatManager;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @ModifyVariable(
            method = "execute",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 2
    )
    private static SignedMessage tweakMessageAtHead(SignedMessage original) {
        return ChatManager.INSTANCE.handleChatMessage(original);
    }
}
