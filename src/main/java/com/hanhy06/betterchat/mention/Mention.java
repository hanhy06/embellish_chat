package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.mention.data.playerdata.PlayerData;
import com.hanhy06.betterchat.mention.data.playerdata.PlayerDataManager;
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

    public Mention(UserCache userCache, PlayerManager manager, Path modDirPath) {
        this.userCache = userCache;
        this.manager = manager;
        this.playerDataManager = new PlayerDataManager(userCache, modDirPath);
    }

    public List<MentionToken> playerMention(UUID sender,String originalMessage, ItemStack item){
        List<MentionToken> tokens = new ArrayList<>();

        for (MentionToken token : mentionParser(originalMessage)){
            UUID uuid;
            PlayerData playerData;

            ServerPlayerEntity player = manager.getPlayer(token.name);

            if (player == null) {
                Optional<GameProfile> optionalGameProfile = userCache.findByName(token.name);
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
                    (item == null) ? null : ItemStack.CODEC
                            .encodeStart(NbtOps.INSTANCE, item)
                            .resultOrPartial(error -> BetterChat.LOGGER.error("Failed to encode ItemStack NBT: {}", error))
                            .map(Object::toString)
                            .orElse(null)
            );
            playerData.addMentionData(data);

            if (ConfigManager.getConfigData().saveMentionEnabled()) playerDataManager.savePlayerData(playerData);
            tokens.add(token);
        }

        return tokens;
    }

    private List<MentionToken> mentionParser(String originalMessage){
        List<MentionToken> tokens = new ArrayList<>();
        if(originalMessage == null || !originalMessage.contains("@")) return tokens;

        Matcher matcher = MENTION_PATTERN.matcher(originalMessage);

        while (matcher.find()){
            tokens.add(new MentionToken(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return  tokens;
    }

    public record MentionToken(
            String name,int begin,int end
    ){}
}