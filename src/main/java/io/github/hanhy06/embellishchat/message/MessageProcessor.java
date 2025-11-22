package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.github.hanhy06.embellishchat.util.PermissionUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingProcessor;
    private final PlayerManager playerManager;

    private Config config;
    private Set<UUID> bannedPlayerList;

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

        List<Mention> mentions = mentionProcessor.handleMention(stringMessage,getPermissions(sender, config.mentionRules().keySet()),sender);
        mentions.sort(Comparator.comparing(Mention::begin));

        textMessage = stylingProcessor.applyMention(textMessage,mentions,sender);
        textMessage = stylingProcessor.handleStyle(textMessage,getPermissions(sender, config.stylingRules().keySet()),sender);

        return message.withUnsignedContent(textMessage);
    }
}