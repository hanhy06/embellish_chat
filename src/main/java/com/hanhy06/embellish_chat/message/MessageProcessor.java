package com.hanhy06.embellish_chat.message;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.mention.MentionTarget;
import com.hanhy06.embellish_chat.mention.ParsedMention;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.minecraft.network.message.SignedMessage;
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
        this.config = newConfig;
        this.bannedPlayerList = config.bannedPlayerList();
    }

    public SignedMessage handleMessage(SignedMessage message) {
        if (bannedPlayerList.contains(message.getSender())) return message;

        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());

        MutableText finalMessage = message.getContent().copy();
        String raw = message.getContent().getString();

//        List<MentionTarget> targets = List.of();
//        if (config.mentionEnabled() && sender != null) {
//            targets = handleMentions(raw,sender);
//            finalMessage = stylingManager.applyMention(
//                    finalMessage,
//                    targets
//            );
//        }

        finalMessage = stylingManager.applyStylingRule(
                finalMessage,
                "chat"
        );

        return message.withUnsignedContent(finalMessage);
    }

//    private List<MentionTarget> handleMentions(String raw, ServerPlayerEntity sender) {
//        List<ParsedMention> parsedMentions = mentionProcessor.parseMentions(raw,"mention");
//        List<MentionTarget> targets = mentionProcessor.identifyMentionTargets(sender, parsedMentions);
//        if (!parsedMentions.isEmpty()) {
//            mentionProcessor.broadcastMention(sender, targets);
//        }
//        return targets;
//    }
}
