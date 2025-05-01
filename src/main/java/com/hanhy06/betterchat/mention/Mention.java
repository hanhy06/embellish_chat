package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.mention.data.PlayerData;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private final UserCache userCache;
    private final PlayerManager manager;
    private final PlayerDataManager playerDataManager;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{3,16})(?=\\b|$)");

    public Mention(UserCache userCache, PlayerManager manager) {
        this.userCache = userCache;
        this.manager = manager;
        this.playerDataManager = new PlayerDataManager(userCache, BetterChat.getModDirectoryPath());
    }

    public List<String> playerMention(UUID sender,String originalMessage, ItemStack item){
        List<String> names = new ArrayList<>();

        for (String name : nameParser(originalMessage)){
            UUID uuid;
            PlayerData playerData;

            ServerPlayerEntity player = manager.getPlayer(name);

            if (player == null) {
                Optional<GameProfile> optionalGameProfile = userCache.findByName(name);
                if(optionalGameProfile.isEmpty()) continue;

                uuid = optionalGameProfile.get().getId();;
                playerData = playerDataManager.loadPlayerData(uuid);
            }else{
                uuid = player.getUuid();
                playerData = playerDataManager.loadPlayerData(uuid);

                if (playerData.isNotificationsEnabled()) player.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP), SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
            }

            MentionData data = new MentionData(
                    sender,
                    Timestamp.timeStamp(),
                    originalMessage,
                    (item == null) ? null : ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, item)
                            .resultOrPartial(error -> BetterChat.LOGGER.error("Failed to encode ItemStack NBT: {}", error))
                            .map(Object::toString)
                            .orElse(null)
            );
            playerData.addMentionData(data);

            playerDataManager.savePlayerData(playerData);
            names.add(name);
        }

        return names;
    }

    private List<String> nameParser(String originalMessage){
        List<String> names = new ArrayList<>();
        if(originalMessage == null || !originalMessage.contains("@")) return names;

        Matcher matcher = MENTION_PATTERN.matcher(originalMessage);

        while (matcher.find()){
            names.add(matcher.group(1));
        }

        return  names;
    }
}