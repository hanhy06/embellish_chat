package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.util.Timestamp;
import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.UserCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private final UserCache userCache;
    private final PlayerDataManager playerDataManager;

    public Mention(UserCache userCache, PlayerManager playerManager1) {
        this.userCache = userCache;
        this.playerDataManager = new PlayerDataManager(userCache, BetterChat.getModDirectoryPath());
    }

    public List<String> playerMention(String originalMessage, ItemStack item){
        List<String> names = nameParser(originalMessage);
        List<String> namesToRemove = new ArrayList<>();

        for (String name : names){
            Optional<GameProfile> profile = userCache.findByName(name);

            if(profile.isEmpty()) {
                namesToRemove.add(name);
                continue;
            }

            UUID uuid = profile.get().getId();
            MentionData data = new MentionData(
                    uuid,
                    Timestamp.timeStamp(),
                    originalMessage,
                    (item == null) ? null : ItemStack.CODEC.encodeStart(NbtOps.INSTANCE,item).toString()
            );

            playerDataManager.addPlayerData(uuid,data);
        }

        names.removeAll(namesToRemove);

        return names;
    }

    private List<String> nameParser(String originalMessage){
        List<String> names = new ArrayList<>();
        if(!originalMessage.contains("@")) return names;

        String regex = "@([A-Za-z0-9_]{3,16})(?=\\b|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalMessage);

        while (matcher.find()){
            names.add(matcher.group(1));
        }

        return  names;
    }
}