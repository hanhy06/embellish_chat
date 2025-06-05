package com.hanhy06.betterchat.config;

import com.google.gson.annotations.JsonAdapter;
import com.hanhy06.betterchat.util.HexIntegerTypeAdapter;

public record ConfigData(
        boolean textPostProcessingEnabled,
        boolean mentionEnabled,
        boolean saveMentionEnabled,
        int maxMentionBufferSize,
        int mentionBufferClearIntervalMinutes,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultMentionColor,
        String defaultMentionNotificationSound,
        int maxPlayerDataCacheSize,
        int playerDataCacheTTLMinutes)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                true,
                true,
                1000,
                10,
                0xFFFF55,
                "minecraft:entity.experience_orb.pickup",
                1000,
                3

        );
    }
}