package io.github.hanhy06.embellish_chat.mixin;

import io.github.hanhy06.embellish_chat.message.MessageProcessor;
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
            argsOnly = true
    )
    private static SignedMessage execute(SignedMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }
}
