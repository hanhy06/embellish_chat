package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChatHandler implements ConfigListener {
    public static ChatHandler INSTANCE;

    private static Config config;
    private static SoundEvent mentionSound;
    private static float mentionSoundPitch;

    public ChatHandler() {
        INSTANCE = this;
        applyConfig(ConfigManager.getConfig());
    }

    public SignedMessage handleChatMessage(Text sender, SignedMessage original) {
        MinecraftServer server = EmbellishChat.server;

        MutableText baseMessage = MutableText.of(original.getContent().getContent());
        String raw = original.getContent().getString();

        List<Receiver> receivers = List.of();
        if (config.mentionEnabled()) {
             receivers = handleMentions(server,raw,sender);
        }

        MutableText finalMessage = baseMessage;
        if (config.inChatStylingEnabled()){
            finalMessage = StyledTextProcessor.applyStyles(config, baseMessage, receivers);
        }

        return original.withUnsignedContent(finalMessage);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        applyConfig(newConfig);
    }

    private static List<Receiver> handleMentions(MinecraftServer server, String raw, Text sender) {
        List<Receiver> receivers = Mention.parseMentions(server, raw);
        if (!receivers.isEmpty()) {
            Mention.broadcastMention(mentionSound, mentionSoundPitch, sender, receivers);
        }
        return receivers;
    }

    private static void applyConfig(Config config) {
        ChatHandler.config = config;
        Identifier id = Identifier.tryParse(config.defaultMentionSound());
        mentionSound = SoundEvent.of(id);
        mentionSoundPitch = config.defaultMentionSoundPitch();
    }
}
