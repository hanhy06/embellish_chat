package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.util.Timestamp;
import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
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

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{3,16})(?=\\b|$)");

    public Mention(UserCache userCache) {
        this.userCache = userCache;
        this.playerDataManager = new PlayerDataManager(userCache, BetterChat.getModDirectoryPath());
    }

    public List<String> playerMention(String originalMessage, ItemStack item){
        List<String> names = new ArrayList<>();

        for (String name : nameParser(originalMessage)){
            Optional<GameProfile> profile = userCache.findByName(name);

            if(profile.isEmpty()) continue;

            UUID uuid = profile.get().getId();
            MentionData data = new MentionData(
                    uuid,
                    Timestamp.timeStamp(),
                    originalMessage,
                    (item == null) ? null : ItemStack.CODEC.encodeStart(NbtOps.INSTANCE,item).toString()
            );

            playerDataManager.addPlayerData(uuid,data);
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