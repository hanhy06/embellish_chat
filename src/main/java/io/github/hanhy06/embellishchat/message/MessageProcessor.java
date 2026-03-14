package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.StyleProcessor;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.*;

import static io.github.hanhy06.embellishchat.util.PermissionUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StyleProcessor styleProcessor;
    private final PlayerManager playerManager;

    private Config config;
    private Set<UUID> bannedPlayerList;
    private LinkedHashMap<String,MutableText> prefix;

    public static class MessageBlockedException extends RuntimeException {}

    public MessageProcessor(MentionProcessor mentionProcessor, StyleProcessor styleProcessor, PlayerManager playerManager) {
        INSTANCE = this;
        this.mentionProcessor = mentionProcessor;
        this.styleProcessor = styleProcessor;
        this.playerManager = playerManager;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.bannedPlayerList = config.banned_players();
        this.prefix = config.prefix();
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
            PlaceHolderUtil.put(message.getSender(),stringMessage);
            List<Mention> mentions = mentionProcessor.handleMention(stringMessage,getPermissions(sender, config.mention_rules().keySet()),sender);
            mentions.sort(Comparator.comparing(Mention::begin));

            textMessage = styleProcessor.applyMention(textMessage,mentions,sender);
            textMessage = styleProcessor.handleStyle(textMessage,getPermissions(sender, config.style_rules().keySet()),sender);

            List<String> prefixKeys = getPermissions(sender, prefix.keySet());
            if (!prefixKeys.isEmpty()){
                MutableText prefix = this.prefix.get(prefixKeys.getLast());
                textMessage = PlaceHolderUtil.parsePlaceholder(prefix,sender).append(textMessage);
            }

            result = message.withUnsignedContent(textMessage);

            mentionProcessor.targetBroadcast(mentions,result,sender);
        } catch (MessageBlockedException block){
            return null;
        } finally {
            PlaceHolderUtil.remove(message.getSender());
        }

        return result;
    }
}