package io.github.hanhy06.embellish_chat.mixin;

import io.github.hanhy06.embellish_chat.message.MessageProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "handleDecoratedMessage", at = @At("HEAD"), argsOnly = true)
    private SignedMessage handleDecoratedMessage(SignedMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }
}
