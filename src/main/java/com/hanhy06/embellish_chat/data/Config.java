package com.hanhy06.embellish_chat.data;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public record Config(
        boolean inChatStylingEnabled,
        boolean mentionEnabled,
        boolean fontEnabled,
        boolean coloringEnabled,
        boolean openUriEnabled,
        boolean markdownEnabled,
        int defaultMentionColor,
        String defaultMentionSound,
        int defaultChatColor,
        String defaultChatFont,
        HashMap<String, Integer> defaultColorPreset
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
                0x0,
                "",
                new HashMap<>(
                        Map.ofEntries(
                                entry("black", 0x000000),
                                entry("dark blue", 0x0000AA),
                                entry("dark green", 0x00AA00),
                                entry("dark aqua", 0x00AAAA),
                                entry("dark red", 0xAA0000),
                                entry("dark purple", 0xAA00AA),
                                entry("gold", 0xFFAA00),
                                entry("gray", 0xAAAAAA),
                                entry("dark gray", 0x555555),
                                entry("blue", 0x5555FF),
                                entry("green", 0x55FF55),
                                entry("aqua", 0x55FFFF),
                                entry("red", 0xFF5555),
                                entry("light purple", 0xFF55FF),
                                entry("yellow", 0xFFFF55),
                                entry("white", 0xFFFFFF)
                        )
                )
        );
    }
}