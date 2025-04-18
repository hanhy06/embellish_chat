package com.hanhy06.betterchat.mixin;

import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.preparation.PreparationText;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifyDecoratedMessage(SignedMessage original) {
        String message = original.getContent().getString();
        Text messagePrep = original.getContent();

        ConfigData configData = ConfigManager.getConfigData();

        if (configData.textPreparationEnabled()){
            messagePrep = PreparationText.prepText(message,player.getServer().getPlayerManager());
        }

        return new SignedMessage(
                MessageLink.of(new UUID(0L, 0L)),
                null,
                MessageBody.ofUnsigned(message),
                messagePrep,
                FilterMask.PASS_THROUGH
        );
    }
}
