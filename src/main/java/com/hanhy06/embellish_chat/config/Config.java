package com.hanhy06.embellish_chat.config;

import com.hanhy06.embellish_chat.styling.StyleType;
import com.hanhy06.embellish_chat.styling.StylingRule;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        //style
        HashMap<String,List<StylingRule>> stylingRules,

        //styling option
        int urlColor,
        HashMap<String, Integer> colorPreset,
        int chatColor,
        String chatFont,

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

        //banned player
        List<UUID> bannedPlayerList
)
{
    public static Config createDefault(){
        return new Config(
                //style
                new HashMap<>(Map.ofEntries(
                        entry("chat",
                                List.of(
                                        StylingRule.of("(.+)()",StyleType.PREPROCESSING_FONT),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", StyleType.COLOR_HEX),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<rainbow>", StyleType.COLOR_RAINBOW),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(.+?)>", StyleType.COLOR_PRESET),
                                        StylingRule.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", StyleType.URL),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", StyleType.FONT),
                                        StylingRule.of("(?<!\\\\)\\*\\*(.+?)\\*\\*()", StyleType.BOLD),
                                        StylingRule.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)()", StyleType.ITALIC),
                                        StylingRule.of("(?<!\\\\)__(.+?)__()", StyleType.UNDERLINE),
                                        StylingRule.of("(?<!\\\\)~~(.+?)~~()", StyleType.STRIKETHROUGH),
                                        StylingRule.of("(?<!\\\\)\\|\\|(.+?)\\|\\|()", StyleType.OBFUSCATED)
                                )
                        ),
                        entry("anvil",
                                List.of(
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", StyleType.COLOR_HEX),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<rainbow>", StyleType.COLOR_RAINBOW),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(.+?)>", StyleType.COLOR_PRESET),
                                        StylingRule.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", StyleType.URL),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", StyleType.FONT),
                                        StylingRule.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", StyleType.BOLD),
                                        StylingRule.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", StyleType.ITALIC),
                                        StylingRule.of("(?<!\\\\)__(.+?)__", StyleType.UNDERLINE),
                                        StylingRule.of("(?<!\\\\)~~(.+?)~~", StyleType.STRIKETHROUGH),
                                        StylingRule.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", StyleType.OBFUSCATED)
                                )
                        ),
                        entry("book",
                                List.of(
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", StyleType.COLOR_HEX),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<rainbow>", StyleType.COLOR_RAINBOW),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(.+?)>", StyleType.COLOR_PRESET),
                                        StylingRule.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", StyleType.URL),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", StyleType.FONT),
                                        StylingRule.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", StyleType.BOLD),
                                        StylingRule.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", StyleType.ITALIC),
                                        StylingRule.of("(?<!\\\\)__(.+?)__", StyleType.UNDERLINE),
                                        StylingRule.of("(?<!\\\\)~~(.+?)~~", StyleType.STRIKETHROUGH),
                                        StylingRule.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", StyleType.OBFUSCATED)
                                )
                        ),
                        entry("sign",
                                List.of(
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", StyleType.COLOR_HEX),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<rainbow>", StyleType.COLOR_RAINBOW),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(.+?)>", StyleType.COLOR_PRESET),
                                        StylingRule.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", StyleType.URL),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", StyleType.FONT),
                                        StylingRule.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", StyleType.BOLD),
                                        StylingRule.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", StyleType.ITALIC),
                                        StylingRule.of("(?<!\\\\)__(.+?)__", StyleType.UNDERLINE),
                                        StylingRule.of("(?<!\\\\)~~(.+?)~~", StyleType.STRIKETHROUGH),
                                        StylingRule.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", StyleType.OBFUSCATED)
                                )
                        ),
                        entry("command",
                                List.of(
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(#.{6})>", StyleType.COLOR_HEX),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<rainbow>", StyleType.COLOR_RAINBOW),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]<(.+?)>", StyleType.COLOR_PRESET),
                                        StylingRule.of("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)", StyleType.URL),
                                        StylingRule.of("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}", StyleType.FONT),
                                        StylingRule.of("(?<!\\\\)\\*\\*(.+?)\\*\\*", StyleType.BOLD),
                                        StylingRule.of("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)", StyleType.ITALIC),
                                        StylingRule.of("(?<!\\\\)__(.+?)__", StyleType.UNDERLINE),
                                        StylingRule.of("(?<!\\\\)~~(.+?)~~", StyleType.STRIKETHROUGH),
                                        StylingRule.of("(?<!\\\\)\\|\\|(.+?)\\|\\|", StyleType.OBFUSCATED)
                                )
                        )
                )),

                //styling option
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
                0x0,
                "",

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

                //banned player
                new ArrayList<>()
        );
    }
}