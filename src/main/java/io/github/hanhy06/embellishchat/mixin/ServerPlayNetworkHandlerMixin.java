package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
    private static void handleDecoratedMessage(SignedMessage message, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "handleDecoratedMessage", at = @At("HEAD"), argsOnly = true)
    private SignedMessage handleDecoratedMessage(SignedMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }
}
