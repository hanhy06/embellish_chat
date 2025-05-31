package com.hanhy06.betterchat.chat.processor;

import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.PlayerDataManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.UserCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private final PlayerDataManager playerDataManager;
    private final PlayerManager manager;
    private final UserCache userCache;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public Mention(PlayerDataManager playerDataManager, PlayerManager manager, UserCache userCache) {
        this.playerDataManager = playerDataManager;
        this.manager = manager;
        this.userCache = userCache;
    }

    public List<MentionUnit> mentionParser(String originalMessage, Text senderName){
        List<MentionUnit> units = new ArrayList<>();

        for (Unit unit : nameParser(originalMessage)){
            Optional<GameProfile> gameProfile = userCache.findByName(unit.name);
            if(gameProfile.isEmpty()) continue;

            UUID uuid = gameProfile.get().getId();

            PlayerData playerData = playerDataManager.getPlayerData(uuid);
            if (playerData == null) continue;

            if (playerData.isNotificationsEnabled()) {
                ServerPlayerEntity player = manager.getPlayer(uuid);
                if (player != null) {
//                    player.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP), SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
                    player.sendMessage(Text.of(senderName+"mention you"));
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1, 1.75F);
                }
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