package com.hanhy06.betterchat.chat.processor;

import com.mojang.serialization.JsonOps;
import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.config.ConfigLoadedListener;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.service.MentionDataService;
import com.hanhy06.betterchat.data.service.PlayerDataService;
import com.hanhy06.betterchat.util.Timestamp;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention implements ConfigLoadedListener {
    private boolean saveMentionEnabled;

    private final PlayerDataService playerDataService;
    private final MentionDataService mentionDataService;

    private final PlayerManager manager;
    private final UserCache userCache;

    private int maxMentionBufferSize;
    private RegistryEntry<SoundEvent> mentionNotificationSound;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public Mention(PlayerDataService playerDataService, MentionDataService mentionDataService, PlayerManager manager, UserCache userCache) {
        this.playerDataService = playerDataService;
        this.mentionDataService = mentionDataService;

        this.manager = manager;
        this.userCache = userCache;
    }

    public void mentionBroadcast(List<MentionUnit> units,Text textMessage,String senderName,UUID senderUUID){
        String jsonText = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE,textMessage).toString();
        String timeStamp = Timestamp.timeStamp();

        if(mentionDataService.getPendingMentionCount() + units.size() > maxMentionBufferSize) mentionDataService.bufferClearProcess();

        for (MentionUnit unit : new HashSet<>(units)){
            UUID uuid = unit.receiver().getPlayerUUID();
            ServerPlayerEntity player = manager.getPlayer(uuid);

            MentionData mentionData = new MentionData(
                    0,
                    uuid,
                    senderUUID,
                    timeStamp,
                    jsonText,
                    null,
                    false
            );

            if(player != null && unit.receiver().isNotificationsEnabled()){
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(mentionNotificationSound, SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
                player.sendMessage(Text.of(senderName+" mentioned you"));
                if (saveMentionEnabled) mentionDataService.writeMentionData(mentionData);
                continue;
            }

            if (saveMentionEnabled) mentionDataService.bufferWrite(mentionData);
        }
    }

    public List<MentionUnit> mentionParser(String originalMessage){
        List<MentionUnit> units = new ArrayList<>();

        for (Unit unit : nameParser(originalMessage)){
            Optional<GameProfile> gameProfile = userCache.findByName(unit.name);
            if(gameProfile.isEmpty()) continue;

            UUID uuid = gameProfile.get().getId();

            PlayerData playerData = playerDataService.getPlayerData(uuid);
            if (playerData.getPlayerName() == null) {
                playerData = new PlayerData(
                        gameProfile.get().getName(),
                        uuid,
                        true,
                        ConfigManager.getConfigData().defaultMentionColor()
                );

                playerDataService.savePlayerData(playerData);
            }

            units.add(new MentionUnit(playerData,unit.begin,unit.end));
        }

        return units;
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
        saveMentionEnabled = newConfigData.saveMentionEnabled();

        maxMentionBufferSize = newConfigData.maxMentionBufferSize();
        mentionNotificationSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(newConfigData.defaultMentionNotificationSound())));
    }

    private record Unit(
            String name,int begin,int end
    ){}
}