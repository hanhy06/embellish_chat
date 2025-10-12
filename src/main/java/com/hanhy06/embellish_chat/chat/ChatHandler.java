package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.data.Target;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

public class ChatHandler implements ConfigListener {
    public static ChatHandler INSTANCE;

    private Config config;
    private List<UUID> bannedPlayerList;

    private final Mention mention;
    private final PlayerManager manager;
    private StyledTextProcessor processor;

    public ChatHandler(PlayerManager manager, Scoreboard scoreboard) {
        INSTANCE = this;
        this.manager = manager;
        this.mention = new Mention(manager,scoreboard);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        applyConfig(newConfig);
    }

    public SignedMessage handleChatMessage(SignedMessage original) {
        if (bannedPlayerList.contains(original.getSender())) return original;

        ServerPlayerEntity sender = manager.getPlayer(original.getSender());

        MutableText baseMessage = MutableText.of(original.getContent().getContent());
        String raw = original.getContent().getString();

        List<Receiver> receivers = List.of();
        if (config.mentionEnabled()) {
             receivers = handleMentions(raw,sender);
        }

        MutableText finalMessage = baseMessage;
        if (config.inChatStylingEnabled()){
            finalMessage = processor.applyStyles(baseMessage, receivers);
        }

        return original.withUnsignedContent(finalMessage);
    }

    private List<Receiver> handleMentions(String raw,ServerPlayerEntity sender) {
        List<Target> targets = mention.parseMentions(raw);
        List<Receiver> receivers = mention.processReceiver(sender,targets);
        if (!targets.isEmpty()) {
            mention.broadcastMention(sender, receivers);
        }
        return receivers;
    }

    private void applyConfig(Config config) {
        this.config = config;
        this.bannedPlayerList = config.bannedPlayerList();

        mention.updateConfig(config);
        processor.updateConfig(config);
    }
}
