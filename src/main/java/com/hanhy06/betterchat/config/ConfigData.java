package com.hanhy06.betterchat.config;

import java.util.List;

public record ConfigData(
        boolean textMarkdownEnabled,
        boolean textFilteringEnabled,
        List<String> textFilteringKeywordList,
        boolean mentionEnabled,
        boolean saveMentionEnabled,
        int defaultMentionColor,
        String defaultMentionNotificationSound)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                false,
                List.of("example","keyword"),
                true,
                true,
                16777045,
                "minecraft:entity.experience_orb.pickup"
        );
    }
}