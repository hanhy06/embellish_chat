package io.github.hanhy06.embellishchat.config;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.util.Map.entry;

public record Config(
        String version,

        //rules
        TreeMap<String,List<StylingRule>> stylingRules,
        TreeMap<String,List<MentionRule>> mentionRules,

        //preset
        String delimiter,
        String timestamp,
        Color urlColor,
        HashMap<String, Color> colorPreset,
        Color colorTeam,
        boolean notificationCommandEnable,

        //player list
        HashSet<UUID> bannedPlayerList,
        HashSet<UUID> notificationOffPlayerList
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
                        entry("embellish-chat.chat",
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
                                                "\\[([^\\]]+?)]<(#.{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#.{6,})>",
                                                List.of(StyleAction.of(StyleType.COLOR_GRADIENT,""))
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
                        entry("embellish-chat.command_argument", List.of())
                )),
                new TreeMap<>(Map.ofEntries(
                    entry(
                            "embellish-chat.mention",
                            List.of(
                                    MentionRule.of(
                                            "@here()",
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.INSIDE,"64"
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    ),
                                                    StyleAction.of(
                                                            StyleType.COLOR_PRESET,"light purple"
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@everyone()",
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.EVERYONE,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    ),
                                                    StyleAction.of(
                                                            StyleType.COLOR_PRESET,"light purple"
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@team\\((.+?)\\)",
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
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
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.LUCK_PERMS_GROUP,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    ),
                                                    StyleAction.of(
                                                            StyleType.COLOR_PRESET,"light purple"
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@world\\((.+?)\\)",
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.WORLD,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    ),
                                                    StyleAction.of(
                                                            StyleType.COLOR_PRESET,"light purple"
                                                    )
                                            )
                                    ),
                                    MentionRule.of(
                                            "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
                                            Identifier.of("minecraft:entity.experience_orb.pickup"),
                                            1.75f,
                                            "%player:displayname% mentioned you",
                                            0,
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
                new  Color(0x0000EE),
                new HashMap<>(Map.ofEntries(
                        entry("black", new Color(0x000000)),
                        entry("dark blue", new Color(0x0000AA)),
                        entry("dark green", new Color(0x00AA00)),
                        entry("dark aqua", new Color(0x00AAAA)),
                        entry("dark red", new Color(0xAA0000)),
                        entry("dark purple", new Color(0xAA00AA)),
                        entry("gold", new Color(0xFFAA00)),
                        entry("gray", new Color(0xAAAAAA)),
                        entry("dark gray", new Color(0x555555)),
                        entry("blue", new Color(0x5555FF)),
                        entry("green", new Color(0x55FF55)),
                        entry("aqua", new Color(0x55FFFF)),
                        entry("red", new Color(0xFF5555)),
                        entry("light purple", new Color(0xFF55FF)),
                        entry("yellow", new Color(0xFFFF55)),
                        entry("white", new Color(0xFFFFFF))
                )),
                new Color(0xFF55FF),
                true,

                //player list
                new HashSet<>(),
                new HashSet<>()
        );
    }
}