package com.hanhy06.embellish_chat.config;

import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import com.hanhy06.embellish_chat.styling.rule.StyleType;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        //style
        //TODO: 나중에 LuckPerms쓸때 각 그룹이 어떤 스타일링을 할지 그룹 이름이 키로 써야함
        HashMap<String,List<StylingRule>> stylingRules,

        //styling preset
        int urlColor,
        HashMap<String, Integer> colorPreset,
        String delimiter,

        //mention
        //TODO: 맨션 방식을 기존에서 스타일링과 비슷하게 정규식,맨션타입,List<StyleAction> 으로 변경하여 더 높은 유연성
        //TODO: 맨션 타입을 기존 team 에서 TEAM_SELF 와 TEAM_OTHER 로 분리 그리고 LuckPerms 와 통합을 위해 LUCK_PERMS_GROUP 추가
        //TODO: 하나의 MentionRule 이 여러개의 맨션 타입을 갖을수 있게 즉 두번 ServerPlayerEntity를 갖고와서 두개의 리스트에 포함된
        //플레이어를 호출 가능하도록 할것 ex 우리팀 사람중 근쳐 32블럭 안에 있는 사람
        HashMap<String,List<MentionRule>> mentionRules,
        Integer mentionColor,
        boolean offlineColorEnabled,
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
                                                "(?<!\\\\)\\*\\*(.+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)__(.+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)_(.+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)~~(.+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\|\\|(.+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
                                        ),
                                        StylingRule.of(
                                                "(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)",
                                                List.of(StyleAction.of(StyleType.URL,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]\\{([^}]+?)}",
                                                List.of(StyleAction.of(StyleType.FONT,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<SD:(#[A-Fa-f0-9]{6}))>",
                                                List.of(StyleAction.of(StyleType.COLOR_SHADOW,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<([a-z\\s]+?)>",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<(RAINBOW)>",
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
                                                "(?<!\\\\)\\*\\*(.+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)__(.+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)_(.+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)~~(.+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\|\\|(.+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
                                        ),
                                        StylingRule.of(
                                                "(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)",
                                                List.of(StyleAction.of(StyleType.URL,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]\\{([^}]+?)}",
                                                List.of(StyleAction.of(StyleType.FONT,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<(#[A-Fa-f0-9]{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<SD:(#[A-Fa-f0-9]{6}))>",
                                                List.of(StyleAction.of(StyleType.COLOR_SHADOW,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<([a-z\\s]+?)>",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET,""))
                                        ),
                                        StylingRule.of(
                                                "(?<!\\\\)\\[(.+?)]<(RAINBOW)>",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW,"0.7"))
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

                //mention
                new HashMap<>(),
                0xff55ff,
                true,
                "minecraft:entity.experience_orb.pickup",
                1.75f,
                "",
                " mentioned you",

                //banned player
                new ArrayList<>()
        );
    }
}