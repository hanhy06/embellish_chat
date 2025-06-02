package com.hanhy06.betterchat.chat.processor;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.PlayerDataManager;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private final PlayerDataManager playerDataManager;
    private final PlayerManager manager;
    private final UserCache userCache;

    private final RegistryEntry<SoundEvent> mentionNotificationSound;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public Mention(PlayerDataManager playerDataManager, PlayerManager manager, UserCache userCache,String sound) {
        this.playerDataManager = playerDataManager;
        this.manager = manager;
        this.userCache = userCache;

        this.mentionNotificationSound = RegistryEntry.of(Registries.SOUND_EVENT.get(Identifier.of(sound)));
    }

    public void mentionBroadcast(List<MentionUnit> units,Text textMessage,String senderName){
        String jsonText = Text.Serialization.toJsonString(textMessage, BetterChat.getServerInstance().getRegistryManager());
        String timeStamp = Timestamp.timeStamp();

        for (MentionUnit unit : new HashSet<>(units)){
            UUID uuid = unit.receiver().getPlayerUUID();

            if(unit.receiver().isNotificationsEnabled()){
                ServerPlayerEntity player = manager.getPlayer(uuid);
                if (player != null) {
                    player.networkHandler.sendPacket(new PlaySoundS2CPacket(mentionNotificationSound, SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
                    player.sendMessage(Text.of(senderName+" mentioned you"));
                }
            }

            playerDataManager.bufferWrite(new MentionData(
                    0,
                    unit.receiver().getPlayerUUID(),
                    uuid,
                    timeStamp,
                    jsonText,
                    null,
                    false
            ));
        }
    }

    public List<MentionUnit> mentionParser(String originalMessage){
        List<MentionUnit> units = new ArrayList<>();

        for (Unit unit : nameParser(originalMessage)){
            Optional<GameProfile> gameProfile = userCache.findByName(unit.name);
            if(gameProfile.isEmpty()) continue;

            UUID uuid = gameProfile.get().getId();

            PlayerData playerData = playerDataManager.getPlayerData(uuid);
            if (playerData.getPlayerName() == null) {
                playerData = new PlayerData(
                        gameProfile.get().getName(),
                        uuid,
                        true,
                        ConfigManager.getConfigData().defaultMentionColor()
                );

                playerDataManager.savePlayerData(playerData);
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

    private record Unit(
            String name,int begin,int end
    ){}
}