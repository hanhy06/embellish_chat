package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

import static io.github.hanhy06.embellishchat.util.PermissionUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingProcessor;
    private final PlayerManager playerManager;

    private Config config;
    private Set<UUID> bannedPlayerList;
    private LinkedHashMap<String,String> prefixes;

    public static class MessageBlockedException extends RuntimeException {}

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
        this.prefixes = config.prefixes();
    }

    public SignedMessage handleMessage(SignedMessage message) {
        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());
        if (sender == null || bannedPlayerList.contains(message.getSender())) {
            return message;
        }

        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();
        SignedMessage result;

        try {
            PlaceHolderUtil.put(sender,stringMessage);
            List<Mention> mentions = mentionProcessor.handleMention(stringMessage,getPermissions(sender, config.mentionRules().keySet()),sender);
            mentions.sort(Comparator.comparing(Mention::begin));

            textMessage = stylingProcessor.applyMention(textMessage,mentions,sender);
            textMessage = stylingProcessor.handleStyle(textMessage,getPermissions(sender, config.stylingRules().keySet()),sender);

            String prefix = prefixes.getOrDefault(getPermissions(sender,prefixes.keySet()).getLast(),"");
            if (!prefix.isEmpty()) textMessage = PlaceHolderUtil.parseText(prefix,sender).copy().append(textMessage);

            result = message.withUnsignedContent(textMessage);

            mentionProcessor.targetBroadcast(mentions,result,sender);
        } catch (MessageBlockedException block){
            return null;
        } finally {
            PlaceHolderUtil.remove(sender);
        }

        return result;
    }
}