package com.hanhy06.betterchat.playerdata;

import com.hanhy06.betterchat.mention.MentionData;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final Map<UUID,PlayerData> playerDataCache;
    private final Map<UUID, List<MentionData>> mentionDataCache;

    public PlayerDataManager() {
        this.playerDataCache = new ConcurrentHashMap<>();
        this.mentionDataCache = new ConcurrentHashMap<>();
    }

    public Map<UUID, PlayerData> getPlayerDataCache() {
        return playerDataCache;
    }

    public Map<UUID, List<MentionData>> getMentionDataCache() {
        return mentionDataCache;
    }
}
