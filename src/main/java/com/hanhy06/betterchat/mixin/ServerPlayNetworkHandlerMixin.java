package com.hanhy06.betterchat.mixin;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.mention.Mention;
import com.hanhy06.betterchat.preparation.Markdown;
import com.hanhy06.betterchat.util.Metadata;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifyDecoratedMessage(SignedMessage original) {
        String stringMessage = original.getContent().getString();
        MutableText textMessage = MutableText.of(original.getContent().getContent());

        ConfigData config = ConfigManager.getConfigData();

        List<Mention.MentionToken> tokens = new ArrayList<>();
        if (config.mentionEnabled()) {
            tokens = BetterChat.getMention().playerMention(player.getUuid(),stringMessage,null);
        }

        if (config.textFilteringEnabled()){
            stringMessage = BetterChat.getFilter().wordBaseFiltering(stringMessage);
        }

        if (config.textPreparationEnabled()){
            textMessage = BetterChat.getMarkdown().markdown(Text.literal(stringMessage),tokens);
        }

        return new SignedMessage(
                MessageLink.of(new UUID(0L, 0L)),
                null,
                MessageBody.ofUnsigned(stringMessage),
                Metadata.metadata(textMessage),
                FilterMask.PASS_THROUGH
        );
    }
}
