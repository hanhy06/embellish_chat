package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.chat.processor.MentionManager;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.data.Target;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.UUID;

public class ChatHandler implements ConfigListener {
    public static ChatHandler INSTANCE;

    private Config config;
    private List<UUID> bannedPlayerList;

    private final MentionManager mentionManager;
    private final PlayerManager playerManager;

    public ChatHandler(PlayerManager playerManager, Scoreboard scoreboard) {
        INSTANCE = this;
        this.playerManager = playerManager;
        this.mentionManager = new MentionManager(playerManager,scoreboard);
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

        List<Receiver> receivers = List.of();
        if (config.mentionEnabled()) {
             receivers = handleMentions(raw,sender);
        }

        MutableText finalMessage = baseMessage;


        return original.withUnsignedContent(finalMessage);
    }

    private List<Receiver> handleMentions(String raw,ServerPlayerEntity sender) {
        List<Target> targets = mentionManager.parseMentions(raw);
        List<Receiver> receivers = mentionManager.processReceiver(sender,targets);
        if (!targets.isEmpty()) {
            mentionManager.broadcastMention(sender, receivers);
        }
        return receivers;
    }

    private void applyConfig(Config config) {
        this.config = config;
        this.bannedPlayerList = config.bannedPlayerList();

        mentionManager.updateConfig(config);
    }
}
