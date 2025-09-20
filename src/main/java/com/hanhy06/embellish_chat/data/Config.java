package com.hanhy06.embellish_chat.data;

import com.google.gson.annotations.JsonAdapter;
import com.hanhy06.embellish_chat.util.HexIntegerTypeAdapter;

import java.awt.*;

public record Config(
        boolean textPostProcessingEnabled,
        boolean openUriEnabled,
        boolean mentionEnabled,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultMentionColor,
        String defaultMentionSound,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultTextColor
)
{
    public static Config createDefault(){
        return new Config(
                true,
                true,
                true,
                0xFFFF55,
                "minecraft:entity.experience_orb.pickup",
                0
        );
    }
}