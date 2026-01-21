package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "execute",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static SignedMessage execute(SignedMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }
}
