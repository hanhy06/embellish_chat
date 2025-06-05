package com.hanhy06.betterchat.config;

public record ConfigData(
        boolean textPostProcessingEnabled,
        boolean mentionEnabled,
        boolean saveMentionEnabled,
        int maxMentionBufferSize,
        int mentionBufferClearIntervalMinutes,
        int defaultMentionColor,
        String defaultMentionNotificationSound)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                true,
                true,
                1000,
                1,
                0xFFFF55,
                "minecraft:entity.experience_orb.pickup"
        );
    }
}