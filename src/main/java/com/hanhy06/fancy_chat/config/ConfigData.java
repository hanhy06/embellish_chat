package com.hanhy06.fancy_chat.config;

import com.google.gson.annotations.JsonAdapter;
import com.hanhy06.fancy_chat.util.HexIntegerTypeAdapter;

public record ConfigData(
        boolean textPostProcessingEnabled,
        boolean mentionEnabled,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultMentionColor,
        String defaultMentionSound)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                true,
                0xFFFF55,
                "minecraft:entity.experience_orb.pickup"
        );
    }
}