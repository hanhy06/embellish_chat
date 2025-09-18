package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Teamcolor;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatHandler implements ConfigListener {
    public static ChatHandler INSTANCE;
    private static Config config;
    private static RegistryEntry<SoundEvent> mentionSound;

    public ChatHandler() {
        INSTANCE = this;
        config = ConfigManager.getConfig();
        mentionSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(config.defaultMentionSound())));
    }

    public SignedMessage handleChatMessage(ServerPlayerEntity sender, SignedMessage original){
        MinecraftServer server = sender.getServer();

        MutableText message = MutableText.of(original.getContent().getContent());
        String raw = original.getContent().getString();

        List<Receiver> receivers = new ArrayList<>();
        if (config.mentionEnabled()) {
            receivers = Mention.mentionParser(server, raw);

            if (!receivers.isEmpty()) {
                Mention.broadcastMention(config,mentionSound, sender, receivers);
            }
        }

        if (config.textPostProcessingEnabled()) {
            message = StyledTextProcessor.applyStyles(config,message, receivers);
        }

        return original.withUnsignedContent(message);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        config = newConfig;
        mentionSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(config.defaultMentionSound())));
    }
}
