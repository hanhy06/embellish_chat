package com.hanhy06.betterchat.config;

public record ConfigData(
        boolean textMarkdownEnabled,
        boolean mentionEnabled,
        boolean saveMentionEnabled,
        int defaultMentionColor,
        String defaultMentionNotificationSound)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                true,
                true,
                16777045,
                "minecraft:entity.experience_orb.pickup"
        );
    }
}