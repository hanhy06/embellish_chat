package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "broadcastChatMessage", at = @At("HEAD"), argsOnly = true)
    private PlayerChatMessage handleDecoratedMessage(PlayerChatMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }

    @Inject(method = "broadcastChatMessage", at = @At("HEAD"), cancellable = true)
    private void handleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
        }
    }
}
