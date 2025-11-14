package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.MentionSegment;
import io.github.hanhy06.embellishchat.mention.data.ParsedMention;
import io.github.hanhy06.embellishchat.mention.data.ParsedTarget;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.hanhy06.embellishchat.util.PermissionUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingProcessor;
    private final PlayerManager playerManager;

    private Config config;
    private List<UUID> bannedPlayerList;

    public MessageProcessor(MentionProcessor mentionProcessor, StylingProcessor stylingProcessor, PlayerManager playerManager) {
        INSTANCE = this;
        this.mentionProcessor = mentionProcessor;
        this.stylingProcessor = stylingProcessor;
        this.playerManager = playerManager;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.bannedPlayerList = config.bannedPlayerList();
    }

    public SignedMessage handleMessage(SignedMessage message) {
        if (bannedPlayerList.contains(message.getSender())) {
            return message;
        }

        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());
        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();

        List<MentionSegment> mentionSegments = handleMention(sender, stringMessage);
        textMessage = stylingProcessor.applyMention(textMessage, mentionSegments);

        for (String key : getPermissions(sender, config.stylingRules().keySet())) {
            textMessage = stylingProcessor.applyStylingRule(textMessage, key,sender);
        }

        return message.withUnsignedContent(textMessage);
    }

    private List<MentionSegment> handleMention(ServerPlayerEntity sender, String message) {
        List<MentionSegment> mentionSegments = new ArrayList<>();
        if (sender == null || message.isBlank()) {
            return mentionSegments;
        }

        List<String> keys = getPermissions(sender, config.mentionRules().keySet());
        Set<ParsedMention> parsedMentions = new HashSet<>();
        for (String key : keys) {
            parsedMentions.addAll(mentionProcessor.parseMentions(message, key));
        }
        List<ParsedTarget> parsedTargets = mentionProcessor.parseTargets(sender, parsedMentions);

        Set<ServerPlayerEntity> targets = new HashSet<>();
        parsedTargets.forEach(target -> targets.addAll(target.targets()));

        if (config.notificationEnable() && !targets.isEmpty()) {
            if (FabricLoader.getInstance().isModLoaded("luckperms")) {
                mentionProcessor.broadcastMentions(
                        sender,
                        targets.stream().filter(LuckPermsUtil::getNotification).collect(Collectors.toSet())
                );
            } else {
                mentionProcessor.broadcastMentions(sender, targets);
            }
        }

        parsedTargets.forEach(target -> mentionSegments.add(target.createMention()));
        mentionSegments.sort(Comparator.comparing(MentionSegment::begin));
        return mentionSegments;
    }
}