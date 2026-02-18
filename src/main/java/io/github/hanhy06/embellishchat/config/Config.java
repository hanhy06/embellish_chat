package io.github.hanhy06.embellishchat.config;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.List;

import static java.util.Map.entry;

public record Config(
        String version,

        //rules
        LinkedHashMap<String,List<StylingRule>> stylingRules,
        LinkedHashMap<String,List<MentionRule>> mentionRules,

        //preset
        HashMap<String, Color> colors,
        HashMap<String, AtlasTextObjectContents> atlas,
        HashSet<String> whitelist,

        //setting
        String delimiter,
        String timestamp,
        String commandAlias,
        Color urlColor,
        Color defaultTeamColor,
        boolean notificationCommandEnable,
        boolean mentionBroadcast,
        boolean useClearFormat,

        //player list
        HashSet<UUID> bannedPlayerList,
        HashSet<UUID> notificationOffPlayerList,

        //discord
        URI webhook
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
                new LinkedHashMap<>(Map.ofEntries(
                        entry("embellish-chat.chat",
                                List.of(
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\((https://.*?)\\)",
                                                List.of(StyleAction.of(StyleType.URL,"")),
                                                "<blue><b>Pattern</b></blue>: [Text](url)\n<dark_aqua><b>Comment</b></dark_aqua>: clickable link with custom text\n"
                                        ),
                                        StylingRule.of(
                                                "((https://\\S+))",
                                                List.of(StyleAction.of(StyleType.URL,"")),
                                                "<blue><b>Pattern</b></blue>: url\n<dark_aqua><b>Comment</b></dark_aqua>: auto-detect clickable link\n"
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\{(.*?)}",
                                                List.of(StyleAction.of(StyleType.FONT,"")),
                                                "<blue><b>Pattern</b></blue>: [Text]{font}\n<dark_aqua><b>Comment</b></dark_aqua>: apply custom font to text\n"
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#.{6})>",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX,"")),
                                                "<blue><b>Pattern</b></blue>: [Text]<#RRGGBB>\n<dark_aqua><b>Comment</b></dark_aqua>: apply hex color\n"
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#.{6,})>",
                                                List.of(StyleAction.of(StyleType.COLOR_GRADIENT,"")),
                                                "<blue><b>Pattern</b></blue>: [Text]<#color1#color2...>\n<dark_aqua><b>Comment</b></dark_aqua>: apply gradient color\n"
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<([a-z\\s]+?)>",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET,"")),
                                                "<blue><b>Pattern</b></blue>: [Text]<preset>\n<dark_aqua><b>Comment</b></dark_aqua>: apply preset color name\n"
                                        ),
                                        StylingRule.of(
                                                "\\*\\*(.+?)\\*\\*()",
                                                List.of(StyleAction.of(StyleType.BOLD,"")),
                                                "<blue><b>Pattern</b></blue>: **Text**\n<dark_aqua><b>Comment</b></dark_aqua>: bold formatting\n"
                                        ),
                                        StylingRule.of(
                                                "__(.+?)__()",
                                                List.of(StyleAction.of(StyleType.UNDERLINE,"")),
                                                "<blue><b>Pattern</b></blue>: __Text__\n<dark_aqua><b>Comment</b></dark_aqua>: underline formatting\n"
                                        ),
                                        StylingRule.of(
                                                "_(.+?)_()",
                                                List.of(StyleAction.of(StyleType.ITALIC,"")),
                                                "<blue><b>Pattern</b></blue>: _Text_\n<dark_aqua><b>Comment</b></dark_aqua>: italic formatting\n"
                                        ),
                                        StylingRule.of(
                                                "~~(.+?)~~()",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH,"")),
                                                "<blue><b>Pattern</b></blue>: ~~Text~~\n<dark_aqua><b>Comment</b></dark_aqua>: strikethrough formatting\n"
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(.+?)\\|\\|()",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED,"")),
                                                "<blue><b>Pattern</b></blue>: ||Text||\n<dark_aqua><b>Comment</b></dark_aqua>: obfuscated text\n"
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(RAINBOW)>",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW,"0.7")),
                                                "<blue><b>Pattern</b></blue>: [Text]<RAINBOW>\n<dark_aqua><b>Comment</b></dark_aqua>: rainbow color\n"
                                        ),
                                        StylingRule.of(
                                                "(.+)()",
                                                List.of(StyleAction.of(StyleType.METADATA,"")),
                                                "<blue><b>Pattern</b></blue>: any text\n<dark_aqua><b>Comment</b></dark_aqua>: metadata capture layer\n"
                                        ),
                                        StylingRule.of(
                                                "(\\[i\\])()",
                                                List.of(StyleAction.of(StyleType.SHOW_ITEM,"")),
                                                "<blue><b>Pattern</b></blue>: [i]\n<dark_aqua><b>Comment</b></dark_aqua>: show held item\n"
                                        ),
                                        StylingRule.of(
                                                "(\\[inv\\])()",
                                                List.of(StyleAction.of(StyleType.SHOW_INVENTORY,"")),
                                                "<blue><b>Pattern</b></blue>: [inv]\n<dark_aqua><b>Comment</b></dark_aqua>: show player inventory\n"
                                        ),
                                        StylingRule.of(
                                                "(\\[end\\])()",
                                                List.of(StyleAction.of(StyleType.SHOW_ENDER_CHEST,"")),
                                                "<blue><b>Pattern</b></blue>: [end]\n<dark_aqua><b>Comment</b></dark_aqua>: show ender chest contents\n"
                                        ),
                                        StylingRule.of(
                                                "(:(.+?):)",
                                                List.of(StyleAction.of(StyleType.ATLAS_PRESET,"")),
                                                "<blue><b>Pattern</b></blue>: :icon:\n<dark_aqua><b>Comment</b></dark_aqua>: atlas emoji/icon preset\n"
                                        )
                                )
                        ),
                        entry("embellish-chat.command_argument", List.of())
                )),
                new LinkedHashMap<>(Map.ofEntries(
                    entry(
                            "embellish-chat.mention",
                            List.of(
                                    MentionRule.of(
                                            "@here()",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
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
                                            ),
                                            "<blue><b>Pattern</b></blue>: @here\n<dark_aqua><b>Comment</b></dark_aqua>: mention players within 64 blocks\n"
                                    ),

                                    MentionRule.of(
                                            "@everyone()",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
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
                                            ),
                                            "<blue><b>Pattern</b></blue>: @everyone\n<dark_aqua><b>Comment</b></dark_aqua>: mention all online players\n"
                                    ),

                                    MentionRule.of(
                                            "@team\\((.+?)\\)",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.TEAM,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            ),
                                            "<blue><b>Pattern</b></blue>: @team(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given scoreboard team\n"
                                    ),

                                    MentionRule.of(
                                            "@group\\((.+?)\\)",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
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
                                            ),
                                            "<blue><b>Pattern</b></blue>: @group(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given LuckPerms group\n"
                                    ),

                                    MentionRule.of(
                                            "@world\\((.+?)\\)",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
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
                                            ),
                                            "<blue><b>Pattern</b></blue>: @world(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given world\n"
                                    ),

                                    MentionRule.of(
                                            "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
                                            "%player:displayname% mentioned you",
                                            0,
                                            false,
                                            Sound.of(
                                                    "minecraft:entity.experience_orb.pickup",
                                                    SoundCategory.UI,1,1.75f
                                            ),
                                            List.of(
                                                    MentionAction.of(
                                                            MentionType.PLAYER,""
                                                    )
                                            ),
                                            List.of(
                                                    StyleAction.of(
                                                            StyleType.BOLD,""
                                                    )
                                            ),
                                            "<blue><b>Pattern</b></blue>: @Player\n<dark_aqua><b>Comment</b></dark_aqua>: mention a specific player\n"
                                    )
                            )

                    )
                )),

                //preset
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
                new HashMap<>(Map.ofEntries(
                        entry("fire", new AtlasTextObjectContents(
                                Identifier.of("minecraft:blocks"),Identifier.of("minecraft:block/campfire_fire"))
                        ),
                        entry("hunger", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:hud/food_full"))
                        ),
                        entry("heart", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:hud/heart/full"))
                        ),
                        entry("yes", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:container/beacon/confirm"))
                        ),
                        entry("no", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:container/beacon/cancel"))
                        ),
                        entry("move", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:mob_effect/wind_charged"))
                        )
                )),
                new HashSet<>(),

                //setting
                ",",
                "yyyy-MM-dd HH:mm:ss",
                "ec",
                new  Color(0x0000EE),
                new Color(0xFF55FF),
                true,
                true,
                false,

                //player list
                new HashSet<>(),
                new HashSet<>(),

                //discord
                URI.create("")
        );
    }
}