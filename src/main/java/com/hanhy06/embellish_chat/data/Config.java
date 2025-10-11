package com.hanhy06.embellish_chat.data;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        //styling
        boolean inChatStylingEnabled,
        boolean fontEnabled,
        boolean coloringEnabled,
        boolean rainbowEnabled,
        boolean openUriEnabled,
        boolean markdownEnabled,
        boolean metadataEnabled,
        HashMap<String, Integer> colorPreset,

        //mention
        boolean mentionEnabled,
        boolean groupMentionOpOnly,
        boolean offlineColorEnabled,
        int mentionColor,
        int groupMentionColor,
        String mentionSound,
        float mentionPitch,
        String mentionTitlePrefix,
        String mentionTitleSuffix,
        double hereRadius,

        //default style
        int chatColor,
        String chatFont,

        //banned player
        List<UUID> bannedPlayerList
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
                true,
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
                ),

                true,
                true,
                true,
                0xFFFF55,
                0x0000AA,
                "minecraft:entity.experience_orb.pickup",
                1.75f,
                "",
                " mentioned you",
                64,

                0x0,
                "",

                new ArrayList<>()
        );
    }
}