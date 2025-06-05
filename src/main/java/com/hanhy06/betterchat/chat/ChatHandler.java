package com.hanhy06.betterchat.chat;

import com.hanhy06.betterchat.chat.processor.Mention;
import com.hanhy06.betterchat.chat.processor.StyledTextProcessor;
import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.util.Metadata;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatHandler {
    private final ConfigData configData;
    private final Mention mention;

    public ChatHandler(ConfigData configData, Mention mention) {
        this.configData = configData;
        this.mention = mention;
    }

    public SignedMessage handleChatMessage(ServerPlayerEntity sender, SignedMessage original){
        String stringMessage = original.getContent().getString();
        MutableText textMessage = MutableText.of(original.getContent().getContent());

        List<MentionUnit> units = new ArrayList<>();
        if (configData.mentionEnabled()) {
            units = mention.mentionParser(stringMessage);
        }

        if (configData.textPostProcessingEnabled()){
            textMessage = StyledTextProcessor.applyStyles(textMessage,units);
        }

        if (configData.saveMentionEnabled()){
            mention.mentionBroadcast(units,textMessage,sender.getName().getString(),sender.getUuid());
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
