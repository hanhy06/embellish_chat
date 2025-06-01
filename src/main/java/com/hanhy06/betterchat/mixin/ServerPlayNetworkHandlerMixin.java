package com.hanhy06.betterchat.mixin;

import com.google.gson.Gson;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.data.PlayerDataManager;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.util.Metadata;
import com.hanhy06.betterchat.util.Timestamp;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Unique
    private static final Gson gson = new Gson();

    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(method = "handleDecoratedMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifyDecoratedMessage(SignedMessage original) {
        String stringMessage = original.getContent().getString();
        MutableText textMessage = MutableText.of(original.getContent().getContent());

        List<MentionUnit> units = new ArrayList<>();
        if (ConfigManager.getConfigData().mentionEnabled()) {
            units = BetterChat.getMention().mentionParser(stringMessage,player.getName().getString());
        }

        if (ConfigManager.getConfigData().textFilteringEnabled()){
            stringMessage = BetterChat.getFilter().wordBaseFiltering(stringMessage);
        }

        if (ConfigManager.getConfigData().textMarkdownEnabled()){
            textMessage = BetterChat.getMarkdown().markdown(Text.literal(stringMessage),units);
        }

        if (ConfigManager.getConfigData().saveMentionEnabled()){
            PlayerDataManager playerDataManager = BetterChat.getPlayerDataManager();
            UUID uuid = player.getUuid();

            Set<MentionUnit> set = new HashSet<>(units);

            for (MentionUnit unit : set){
                playerDataManager.bufferWrite(new MentionData(
                        0,
                        unit.receiver().getPlayerUUID(),
                        uuid,
                        Timestamp.timeStamp(),
                        gson.toJson(textMessage),
                        null,
                        false
                ));
            }
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
