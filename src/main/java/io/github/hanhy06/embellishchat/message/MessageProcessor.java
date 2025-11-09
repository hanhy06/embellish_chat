package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.ParsedMention;
import io.github.hanhy06.embellishchat.mention.data.ParsedTarget;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
import io.github.hanhy06.embellishchat.styling.data.ParsedStyle;
import io.github.hanhy06.embellishchat.styling.data.StyleSegment;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
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
    private final StylingProcessor stylingManager;
    private final PlayerManager playerManager;

    private Config config;
    private List<UUID> bannedPlayerList;

    public MessageProcessor(MentionProcessor mentionProcessor, StylingProcessor stylingManager, PlayerManager playerManager) {
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
        if (bannedPlayerList.contains(message.getSender())) {
            return message;
        }

        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());
        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();

        List<Mention> mentions = handleMention(sender, stringMessage);
        textMessage = handleStyle(sender, textMessage, mentions);

        return message.withUnsignedContent(textMessage);
    }

    private List<Mention> handleMention(ServerPlayerEntity sender, String message) {
        List<Mention> mentions = new ArrayList<>();
        if (sender == null || message.isBlank()) {
            return mentions;
        }

        List<String> keys = getPermissions(sender, config.mentionRules().keySet());
        Set<ParsedMention> parsedMentions = new HashSet<>();
        for (String key : keys) {
            parsedMentions.addAll(mentionProcessor.parseMentions(message, key));
        }
        List<ParsedTarget> parsedTargets = mentionProcessor.parseTargets(sender, parsedMentions);

        Set<ServerPlayerEntity> targets = new HashSet<>();
        parsedTargets.forEach(target -> targets.addAll(target.players()));

        if (!targets.isEmpty()) {
            if (FabricLoader.getInstance().isModLoaded("luckperms")) {
                mentionProcessor.broadcastMentions(
                        sender,
                        targets.stream().filter(LuckPermsUtil::getNotification).collect(Collectors.toSet())
                );
            } else {
                mentionProcessor.broadcastMentions(sender, targets);
            }
        }

        parsedTargets.forEach(target -> mentions.add(target.createMention()));
        mentions.sort(Comparator.comparing(Mention::begin));
        return mentions;
    }

    private MutableText handleStyle(ServerPlayerEntity sender, MutableText message,List<Mention> mentions){
        MutableText result = stylingManager.applyMention(message,mentions);
        String text = message.getString();
        if (sender == null || message.getString().isBlank()) {
            return result;
        }

        List<String> keys = getPermissions(sender, config.stylingRules().keySet());
        Map<StylingRule,List<ParsedStyle>> parsedStyles = new LinkedHashMap<>();

        for (String key : keys){
            parsedStyles.putAll(stylingManager.parsedStyles(text,key));
        }
        for (StylingRule rule : parsedStyles.keySet()){
            List<StyleSegment> segments = parsedStyles
                    .get(rule)
                    .stream()
                    .map(stylingManager::parsedSegment)
                    .toList();

            result = stylingManager.applyStyle(result,segments);
        }

        return result;
    }
}