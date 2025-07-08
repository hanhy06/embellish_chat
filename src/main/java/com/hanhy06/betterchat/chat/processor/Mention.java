package com.hanhy06.betterchat.chat.processor;

import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.config.ConfigLoadedListener;
import com.hanhy06.betterchat.model.Receiver;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention implements ConfigLoadedListener {
    private final PlayerManager manager;
    private final UserCache userCache;

    private RegistryEntry<SoundEvent> mentionNotificationSound;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public Mention(PlayerManager manager, UserCache userCache) {
        this.manager = manager;
        this.userCache = userCache;
    }

    public void mentionBroadcast(GameProfile sender,Text message,List<Receiver> receivers){
        for (Receiver receiver : new HashSet<>(receivers)){
            UUID uuid = receiver.profile().getId();
            ServerPlayerEntity player = manager.getPlayer(uuid);

            if(player != null){
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(mentionNotificationSound, SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
                player.sendMessage(Text.of(receiver.profile().getName()+" mentioned you"));
            }
        }
    }

    public List<Receiver> mentionParser(String originalMessage){
        List<Receiver> receivers = new ArrayList<>();

        for (Unit unit : nameParser(originalMessage)){
            Optional<GameProfile> profile = userCache.findByName(unit.name);

            if(profile.isEmpty()) continue;

            receivers.add(new Receiver(profile.get(),unit.begin, unit.end));
        }

        return receivers;
    }

    private List<Unit> nameParser(String originalMessage){
        List<Unit> unit = new ArrayList<>();
        if(originalMessage == null || !originalMessage.contains("@")) return unit;

        Matcher matcher = MENTION_PATTERN.matcher(originalMessage);

        while (matcher.find()){
            unit.add(new Unit(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return  unit;
    }

    @Override
    public void onConfigLoaded(ConfigData newConfigData) {
        mentionNotificationSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(newConfigData.defaultMentionNotificationSound())));
    }

    private record Unit(
            String name,int begin,int end
    ){}
}