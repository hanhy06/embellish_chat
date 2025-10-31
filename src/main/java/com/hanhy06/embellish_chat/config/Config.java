package com.hanhy06.embellish_chat.config;

import com.hanhy06.embellish_chat.mention.rule.MentionAction;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.mention.rule.MentionType;
import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import com.hanhy06.embellish_chat.styling.rule.StyleType;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        //Rules
        //TODO: 나중에 Rule에 description 즉 설명 필드 추가해야함
        HashMap<String,List<StylingRule>> stylingRules,
        HashMap<String,List<MentionRule>> mentionRules,

        //styling preset
        int urlColor,
        HashMap<String, Integer> colorPreset,
        String delimiter,

        //option preset
        Integer mentionColor,
        String mentionSound,
        float mentionPitch,
        String mentionTitlePrefix,
        String mentionTitleSuffix,

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
                                        StylingRule.of(
                                                "\\*\\*([^(?!*+$).+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "__(^(?!_+$).+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "_(^(?!_+$).+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "~~(^(?!~+$).+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(^(?!\\|+$).+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\((.*?)\\)",
                                                List.of(StyleAction.of(StyleType.URL,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\{(.*?)}",
                                                List.of(StyleAction.of(StyleType.FONT,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<SD:(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_SHADOW,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<([a-z\\s]+?)>",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(RAINBOW)>",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW,"0.7"))
                                        ),
                                        StylingRule.of(
                                                "(.+)()",
                                                List.of(StyleAction.of(StyleType.METADATA,""))
                                        )
                                )
                        ),
                        entry("command",
                                List.of(
                                        StylingRule.of(
                                                "\\*\\*([^(?!*+$).+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "__(^(?!_+$).+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "_(^(?!_+$).+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "~~(^(?!~+$).+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(^(?!\\|+$).+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\((.*?)\\)",
                                                List.of(StyleAction.of(StyleType.URL,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\{(.*?)}",
                                                List.of(StyleAction.of(StyleType.FONT,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<SD:(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_SHADOW,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<([a-z\\s]+?)>",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(RAINBOW)>",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW,"0.7"))
                                        )
                                )
                        )
                )),
                new HashMap<>(Map.ofEntries(
                    entry(
                            "option",
                            List.of(
                                    MentionRule.of(
                                            "@here()",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.HERE,"64"
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@everyone()",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.EVERYONE,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@team\\((.*?)\\)",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.TEAM,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.PLAYER,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            )
                                    )
                            )
                    )
                )),

                //styling preset
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
                "-",

                //option preset
                0xff55ff,
                "minecraft:entity.experience_orb.pickup",
                1.75f,
                "",
                " mentioned you",

                //banned player
                new ArrayList<>()
        );
    }
}