package com.hanhy06.embellish_chat.message;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.hanhy06.embellish_chat.util.PermissionUtil.getPermissionsKeys;

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

        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();

        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());

        List<Mention> mentions = new ArrayList<>();
        for (String key: getPermissionsKeys(sender,"option",config.mentionRules().keySet())){
            List<Mention> mention = mentionProcessor.handleMention(
                    sender,stringMessage,key
            );

            mentions.addAll(mention);
        }
        mentions.sort(Comparator.comparing(Mention::begin));

        textMessage = stylingManager.applyMention(textMessage,mentions);
        for (String key: getPermissionsKeys(sender,"chat",config.stylingRules().keySet())){
            textMessage = stylingManager.applyStylingRule(textMessage,key);
        }

        return message.withUnsignedContent(textMessage);
    }
}
