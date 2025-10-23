package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.mention.MentionTarget;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.mention.ParsedMention;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.UUID;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private Config config;
    private List<UUID> bannedPlayerList;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingManager;
    private final PlayerManager playerManager;

    public MessageProcessor(MentionProcessor mentionProcessor, StylingProcessor stylingManager,PlayerManager playerManager) {
        INSTANCE = this;
        this.mentionProcessor = mentionProcessor;
        this.stylingManager = stylingManager;
        this.playerManager = playerManager;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        applyConfig(newConfig);
    }

    public SignedMessage handleChatMessage(SignedMessage original) {
        if (bannedPlayerList.contains(original.getSender())) return original;

        ServerPlayerEntity sender = playerManager.getPlayer(original.getSender());

        MutableText baseMessage = original.getContent().copy();
        String raw = original.getContent().getString();

        List<MentionTarget> mentionTargets = List.of();
        if (config.mentionEnabled()) {
             mentionTargets = handleMentions(raw,sender);
        }

        MutableText finalMessage = baseMessage;
        if (!config.inChatStyling().isEmpty()){
            finalMessage = stylingManager.applyStyles(
                    stylingManager.inChatStyling,
                    finalMessage
            );
        }

        return original.withUnsignedContent(finalMessage);
    }

    private List<MentionTarget> handleMentions(String raw, ServerPlayerEntity sender) {
        List<ParsedMention> parsedMentions = mentionProcessor.parseMentions(raw);
        List<MentionTarget> mentionTargets = mentionProcessor.processReceiver(sender, parsedMentions);
        if (!parsedMentions.isEmpty()) {
            mentionProcessor.broadcastMention(sender, mentionTargets);
        }
        return mentionTargets;
    }

    private void applyConfig(Config config) {
        this.config = config;
        this.bannedPlayerList = config.bannedPlayerList();

        mentionProcessor.updateConfig(config);
    }
}
