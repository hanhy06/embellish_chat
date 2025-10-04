package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;

public class ChatHandler implements ConfigListener {
    public static ChatHandler INSTANCE;

    private Config config;
    private final Mention mention;
    private final StyledTextProcessor processor;
    private final PlayerManager manager;

    public ChatHandler(PlayerManager manager, Scoreboard scoreboard) {
        INSTANCE = this;
        this.manager = manager;
        this.mention = new Mention(manager,scoreboard);
        this.processor = new StyledTextProcessor();
    }

    @Override
    public void onConfigReload(Config newConfig) {
        applyConfig(newConfig);
    }

    public SignedMessage handleChatMessage(SignedMessage original) {
        ServerPlayerEntity sender = manager.getPlayer(original.getSender());

        MutableText baseMessage = MutableText.of(original.getContent().getContent());
        String raw = original.getContent().getString();

        List<Receiver> receivers = List.of();
        if (config.mentionEnabled()) {
            receivers = handleMentions(raw,sender);
        }

        MutableText finalMessage = baseMessage;
        if (config.inChatStylingEnabled()){
            finalMessage = processor.applyStyles(baseMessage, receivers);
        }

        return original.withUnsignedContent(finalMessage);
    }

    private List<Receiver> handleMentions(String raw,ServerPlayerEntity sender) {
        List<Receiver> receivers = mention.parseMentions(raw);
        if (!receivers.isEmpty()) {
            mention.broadcastMention(sender, receivers);
        }
        return receivers;
    }

    private void applyConfig(Config config) {
        this.config = config;
        mention.updateConfig(config);
        processor.updateConfig(config);
    }
}
