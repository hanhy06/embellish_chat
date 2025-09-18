package com.hanhy06.embellish_chat.chat;

import com.hanhy06.embellish_chat.chat.processor.Mention;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.config.ConfigManager;
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
        PlayerManager manager = server.getPlayerManager();

        MutableText message = MutableText.of(original.getContent().getContent());
        String raw = original.getContent().getString();

        List<Receiver> receivers = new ArrayList<>();
        if (config.mentionEnabled()) {
            receivers = Mention.mentionParser(server.getUserCache(), raw)
                    .stream()
                    .map(r -> new Receiver(
                            r.profile(),
                            r.begin(),
                            r.end(),
                            resolveTeamColor(manager, server, r.profile().getId(), r.profile().getName())
                    ))
                    .collect(Collectors.toList());

            if (!receivers.isEmpty()) {
                Mention.broadcastMention(mentionSound, sender, receivers);
            }
        }

        if (config.textPostProcessingEnabled()) {
            message = StyledTextProcessor.applyStyles(message, receivers);
            mentionSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(config.defaultMentionSound())));
        }

        return original.withUnsignedContent(message);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        config = newConfig;
    }

    private static int resolveTeamColor(
            PlayerManager manager,
            MinecraftServer server,
            UUID playerId,
            String playerName
    ) {
        int color = Teamcolor.getPlayerColor(manager.getPlayer(playerId));
        if (color != -1) return color;
        return Teamcolor.getPlayerColor(server.getScoreboard(), playerName);
    }
}
