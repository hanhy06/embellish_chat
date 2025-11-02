package com.hanhy06.embellish_chat.message;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.mention.data.ParsedMention;
import com.hanhy06.embellish_chat.mention.data.ParsedTarget;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.*;

import static com.hanhy06.embellish_chat.util.LuckPermsUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingManager;
    private final PlayerManager playerManager;

    private Config config;
    private List<UUID> bannedPlayerList;

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
        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();

        List<Mention> mentions = handleMention(sender,stringMessage);
        textMessage = stylingManager.applyMention(textMessage,mentions);

        for (String key: getPermissions(sender,"chat",config.stylingRules().keySet())){
            textMessage = stylingManager.applyStylingRule(textMessage,key);
        }

        return message.withUnsignedContent(textMessage);
    }

    private List<Mention> handleMention(ServerPlayerEntity sender,String message){
        List<Mention> mentions = new ArrayList<>();
        if (sender ==null || message.isBlank()) return mentions;

        List<String> keys = getPermissions(sender,"mention",config.mentionRules().keySet());
        Set<ParsedMention> parsedMentions = new HashSet<>();
        for (String key : keys){
            parsedMentions.addAll(mentionProcessor.parseMentions(message,key));
        }

        List<ParsedTarget> parsedTargets = mentionProcessor.parseTargets(sender,parsedMentions);

        Set<ServerPlayerEntity> targets = new HashSet<>();
        parsedTargets.forEach(target -> targets.addAll(target.players()));
        if (!targets.isEmpty())mentionProcessor.broadcastMentions(sender,targets);

        parsedTargets.forEach(target -> mentions.add(target.createMention()));
        mentions.sort(Comparator.comparing(Mention::begin));
        return mentions;
    }
}
