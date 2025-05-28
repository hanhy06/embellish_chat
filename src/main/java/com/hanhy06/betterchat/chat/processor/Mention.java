package com.hanhy06.betterchat.chat.processor;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.MentionUnit;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.PlayerDataManager;
import com.hanhy06.betterchat.util.Timestamp;
import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UserCache;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private final PlayerDataManager playerDataManager;
    private final PlayerManager manager;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{3,16})(?=\\b|$)");

    public Mention(PlayerDataManager playerDataManager, PlayerManager manager) {
        this.playerDataManager = playerDataManager;
        this.manager = manager;
    }

    public List<MentionUnit> mentionParser(String originalMessage){
        List<MentionUnit> units = new ArrayList<>();

        for (Unit unit : nameParser(originalMessage)){
            PlayerData playerData = null; playerData = playerDataManager.getPlayerData(unit.name);

            if (playerData == null) continue;
            if (playerData.isNotificationsEnabled()) Objects.requireNonNull(manager.getPlayer(unit.name)).networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP), SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));

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