package com.hanhy06.embellish_chat.data;

import com.hanhy06.embellish_chat.styling.StyleRegex;
import com.hanhy06.embellish_chat.styling.TextStyleApplier;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        //style
        List<StyleRegex> inChatStyling,
        List<StyleRegex> inCommandStyling,
        List<StyleRegex> inAnvilStyling,

        //styling option
        boolean metadataEnabled,
        int urlColor,
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
                //style
                List.of(
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", TextStyleApplier.COLOR_HEX),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<rainbow>", TextStyleApplier.COLOR_RAINBOW),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(.+?)>", TextStyleApplier.COLOR_PRESET),
                        StyleRegex.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", TextStyleApplier.URL),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", TextStyleApplier.FONT),
                        StyleRegex.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", TextStyleApplier.BOLD),
                        StyleRegex.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", TextStyleApplier.ITALIC),
                        StyleRegex.of("(?<!\\\\)__(.+?)__", TextStyleApplier.UNDERLINE),
                        StyleRegex.of("(?<!\\\\)~~(.+?)~~", TextStyleApplier.STRIKETHROUGH),
                        StyleRegex.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", TextStyleApplier.OBFUSCATED)
                ),
                List.of(
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", TextStyleApplier.COLOR_HEX),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<rainbow>", TextStyleApplier.COLOR_RAINBOW),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(.+?)>", TextStyleApplier.COLOR_PRESET),
                        StyleRegex.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", TextStyleApplier.URL),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", TextStyleApplier.FONT),
                        StyleRegex.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", TextStyleApplier.BOLD),
                        StyleRegex.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", TextStyleApplier.ITALIC),
                        StyleRegex.of("(?<!\\\\)__(.+?)__", TextStyleApplier.UNDERLINE),
                        StyleRegex.of("(?<!\\\\)~~(.+?)~~", TextStyleApplier.STRIKETHROUGH),
                        StyleRegex.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", TextStyleApplier.OBFUSCATED)
                ),
                List.of(
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", TextStyleApplier.COLOR_HEX),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<rainbow>", TextStyleApplier.COLOR_RAINBOW),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]<(.+?)>", TextStyleApplier.COLOR_PRESET),
                        StyleRegex.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", TextStyleApplier.URL),
                        StyleRegex.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", TextStyleApplier.FONT),
                        StyleRegex.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", TextStyleApplier.BOLD),
                        StyleRegex.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", TextStyleApplier.ITALIC),
                        StyleRegex.of("(?<!\\\\)__(.+?)__", TextStyleApplier.UNDERLINE),
                        StyleRegex.of("(?<!\\\\)~~(.+?)~~", TextStyleApplier.STRIKETHROUGH),
                        StyleRegex.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", TextStyleApplier.OBFUSCATED)
                ),

                //styling option
                true,
                0x0000EE,
                new HashMap<>(Map.ofEntries(
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
                )),

                //mention
                true,
                true,
                true,
                0xFFFF55,
                0xAAAAFF,
                "minecraft:entity.experience_orb.pickup",
                1.75f,
                "",
                " mentioned you",
                64,

                //default style
                0x0,
                "",

                //banned player
                new ArrayList<>()
        );
    }
}