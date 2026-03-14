package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.message.MessageProcessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MsgCommand.class)
public class MessageCommandMixin {
    @ModifyVariable(method = "sendMessage", at = @At("HEAD"), argsOnly = true)
    private static PlayerChatMessage execute(PlayerChatMessage original) {
        return MessageProcessor.INSTANCE.handleMessage(original);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private static void execute(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage message, CallbackInfo ci) {
        if (message == null) {
            ci.cancel();
        }
    }
}
