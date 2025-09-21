package com.hanhy06.embellish_chat.data;

import com.google.gson.annotations.JsonAdapter;
import com.hanhy06.embellish_chat.util.HexIntegerTypeAdapter;

public record Config(
        boolean inChatStylingEnabled,
        boolean markdownEnabled,
        boolean openUriEnabled,
        boolean coloringEnabled,
        boolean fontEnabled,
        boolean mentionEnabled,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultMentionColor,
        String defaultMentionSound,
        @JsonAdapter(HexIntegerTypeAdapter.class) int defaultChatColor,
        String defaultChatFont
)
{
    public static Config createDefault(){
        return new Config(
                true,
                true,
                true,
                true,
                true,
                true,
                0xFFFF55,
                "minecraft:entity.experience_orb.pickup",
                0,
                ""
        );
    }
}