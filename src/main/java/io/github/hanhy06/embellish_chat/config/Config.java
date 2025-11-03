package io.github.hanhy06.embellish_chat.config;

import io.github.hanhy06.embellish_chat.EmbellishChat;
import io.github.hanhy06.embellish_chat.mention.rule.MentionAction;
import io.github.hanhy06.embellish_chat.mention.rule.MentionRule;
import io.github.hanhy06.embellish_chat.mention.rule.MentionType;
import io.github.hanhy06.embellish_chat.styling.rule.StyleAction;
import io.github.hanhy06.embellish_chat.styling.rule.StyleType;
import io.github.hanhy06.embellish_chat.styling.rule.StylingRule;
import net.fabricmc.loader.api.FabricLoader;

import java.util.*;

import static java.util.Map.entry;

public record Config(
        String version,

        //rules
        //TODO: 나중에 Rule에 description 즉 설명 필드 추가해야함
        Map<String,List<StylingRule>> stylingRules,
        Map<String,List<MentionRule>> mentionRules,

        //preset
        String delimiter,
        String timestamp,
        int urlColor,
        HashMap<String, Integer> colorPreset,
        Integer mentionColor,
        String mentionSound,
        float mentionPitch,
        String mentionTitlePrefix,
        String mentionTitleSuffix,

        //banned player list
        List<UUID> bannedPlayerList
)
{
    public static Config createDefault(){
        return new Config(
                FabricLoader.getInstance()
                        .getModContainer(EmbellishChat.MOD_ID)
                        .orElseThrow()
                        .getMetadata()
                        .getVersion()
                        .getFriendlyString(),

                //style
                new TreeMap<>(Map.ofEntries(
                        entry("embellish_chat.chat",
                                List.of(
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\((https://.*?)\\)",
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
                                                "\\*\\*(.+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "__(.+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "_(.+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "~~(.+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(.+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
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
                        entry("embellish_chat.command_argument",
                                List.of(
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\((https://.*?)\\)",
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
                                                "\\*\\*(.+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,""))
                                        ),
                                        StylingRule.of(
                                                "__(.+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,""))
                                        ),
                                        StylingRule.of(
                                                "_(.+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,""))
                                        ),
                                        StylingRule.of(
                                                "~~(.+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,""))
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(.+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(RAINBOW)>",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW,"0.7"))
                                        )
                                )
                        )
                )),
                new TreeMap<>(Map.ofEntries(
                    entry(
                            "embellish_chat.mention",
                            List.of(
                                    MentionRule.of(
                                            "@here()",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.INSIDE,"64"
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
                                            "@team\\((.+?)\\)",
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
                                            "@group\\((.+?)\\)",
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.LUCK_PERMS_GROUP,""
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

                //preset
                ",",
                "yyyy-MM-dd HH:mm:ss",
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
                0xff55ff,
                "minecraft:entity.experience_orb.pickup",
                1.75f,
                "",
                " mentioned you",

                //banned player list
                new ArrayList<>()
        );
    }
}