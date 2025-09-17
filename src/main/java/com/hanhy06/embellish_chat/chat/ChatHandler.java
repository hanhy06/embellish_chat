package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigData;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Receiver;
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
    private ConfigData configData;
    private final Mention mention;

    public ChatHandler(Mention mention) {
        this.mention = mention;
        this.configData = ConfigManager.INSTANCE.config;
    }

    public SignedMessage handleChatMessage(ServerPlayerEntity sender, SignedMessage original){
        MutableText message = MutableText.of(original.getContent().getContent());

        List<Receiver> receivers = new ArrayList<>();
        if (configData.mentionEnabled()) {
            receivers = mention.mentionParser(original.getContent().getString());
            mention.mentionBroadcast(sender,receivers);
        }

        if (configData.textPostProcessingEnabled()){
            message = StyledTextProcessor.applyStyles(message,receivers);
        }

        return new SignedMessage(
                MessageLink.of(new UUID(0L, 0L)),
                null,
                MessageBody.ofUnsigned(message.getString()),
                message,
                FilterMask.PASS_THROUGH
        );
    }
}
