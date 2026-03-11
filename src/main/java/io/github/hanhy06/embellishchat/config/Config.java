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
import net.minecraft.text.MutableText;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.util.Map.entry;

public record Config(
        String version,

        //rules
        LinkedHashMap<String,List<StylingRule>> style_rules,
        LinkedHashMap<String,List<MentionRule>> mention_rules,

        //preset
        HashMap<String, Color> color,
        HashMap<String, AtlasTextObjectContents> atlas,
        HashSet<String> whitelist,
        LinkedHashMap<String, MutableText> prefix,

        //setting
        String delimiter,
        String timestamp,
        String command_alias,
        Color url_color,
        Color team_color,
        boolean notify_command_enabled,
        boolean notify_mention_enabled,
        boolean disable_vanilla_chat_format,

        //player list
        HashSet<UUID> banned_players,
        HashSet<UUID> notify_off_players
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
                                                "<blue><b>Pattern</b></blue>: [Text](url)\n<dark_aqua><b>Comment</b></dark_aqua>: clickable link with custom text\n",
                                                List.of(StyleAction.of(StyleType.URL, ""))
                                        ),
                                        StylingRule.of(
                                                "((https://\\S+))",
                                                "<blue><b>Pattern</b></blue>: url\n<dark_aqua><b>Comment</b></dark_aqua>: auto-detect clickable link\n",
                                                List.of(StyleAction.of(StyleType.URL, ""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]\\{(.*?)}",
                                                "<blue><b>Pattern</b></blue>: [Text]{font}\n<dark_aqua><b>Comment</b></dark_aqua>: apply custom font to text\n",
                                                List.of(StyleAction.of(StyleType.FONT, ""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#.{6})>",
                                                "<blue><b>Pattern</b></blue>: [Text]<#RRGGBB>\n<dark_aqua><b>Comment</b></dark_aqua>: apply hex color\n",
                                                List.of(StyleAction.of(StyleType.COLOR_HEX, ""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(#.{6,})>",
                                                "<blue><b>Pattern</b></blue>: [Text]<#color1#color2...>\n<dark_aqua><b>Comment</b></dark_aqua>: apply gradient color\n",
                                                List.of(StyleAction.of(StyleType.COLOR_GRADIENT, ""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<([a-z\\s]+?)>",
                                                "<blue><b>Pattern</b></blue>: [Text]<preset>\n<dark_aqua><b>Comment</b></dark_aqua>: apply preset color name\n",
                                                List.of(StyleAction.of(StyleType.COLOR_PRESET, ""))
                                        ),
                                        StylingRule.of(
                                                "\\*\\*(.+?)\\*\\*()",
                                                "<blue><b>Pattern</b></blue>: **Text**\n<dark_aqua><b>Comment</b></dark_aqua>: bold formatting\n",
                                                List.of(StyleAction.of(StyleType.BOLD, ""))
                                        ),
                                        StylingRule.of(
                                                "__(.+?)__()",
                                                "<blue><b>Pattern</b></blue>: __Text__\n<dark_aqua><b>Comment</b></dark_aqua>: underline formatting\n",
                                                List.of(StyleAction.of(StyleType.UNDERLINE, ""))
                                        ),
                                        StylingRule.of(
                                                "_(.+?)_()",
                                                "<blue><b>Pattern</b></blue>: _Text_\n<dark_aqua><b>Comment</b></dark_aqua>: italic formatting\n",
                                                List.of(StyleAction.of(StyleType.ITALIC, ""))
                                        ),
                                        StylingRule.of(
                                                "~~(.+?)~~()",
                                                "<blue><b>Pattern</b></blue>: ~~Text~~\n<dark_aqua><b>Comment</b></dark_aqua>: strikethrough formatting\n",
                                                List.of(StyleAction.of(StyleType.STRIKETHROUGH, ""))
                                        ),
                                        StylingRule.of(
                                                "\\|\\|(.+?)\\|\\|()",
                                                "<blue><b>Pattern</b></blue>: ||Text||\n<dark_aqua><b>Comment</b></dark_aqua>: obfuscated text\n",
                                                List.of(StyleAction.of(StyleType.OBFUSCATED, ""))
                                        ),
                                        StylingRule.of(
                                                "\\[([^\\]]+?)]<(RAINBOW)>",
                                                "<blue><b>Pattern</b></blue>: [Text]<RAINBOW>\n<dark_aqua><b>Comment</b></dark_aqua>: rainbow color\n",
                                                List.of(StyleAction.of(StyleType.COLOR_RAINBOW, "0.7"))
                                        ),
                                        StylingRule.of(
                                                "(.+)()",
                                                "<blue><b>Pattern</b></blue>: any text\n<dark_aqua><b>Comment</b></dark_aqua>: metadata capture layer\n",
                                                List.of(StyleAction.of(StyleType.METADATA, ""))
                                        ),
                                        StylingRule.of(
                                                "(\\[i\\])()",
                                                "<blue><b>Pattern</b></blue>: [i]\n<dark_aqua><b>Comment</b></dark_aqua>: show held item\n",
                                                List.of(StyleAction.of(StyleType.SHOW_ITEM, ""))
                                        ),
                                        StylingRule.of(
                                                "(\\[inv\\])()",
                                                "<blue><b>Pattern</b></blue>: [inv]\n<dark_aqua><b>Comment</b></dark_aqua>: show player inventory\n",
                                                List.of(StyleAction.of(StyleType.SHOW_INVENTORY, ""))
                                        ),
                                        StylingRule.of(
                                                "(\\[end\\])()",
                                                "<blue><b>Pattern</b></blue>: [end]\n<dark_aqua><b>Comment</b></dark_aqua>: show ender chest contents\n",
                                                List.of(StyleAction.of(StyleType.SHOW_ENDER_CHEST, ""))
                                        ),
                                        StylingRule.of(
                                                "(:(.+?):)",
                                                "<blue><b>Pattern</b></blue>: :icon:\n<dark_aqua><b>Comment</b></dark_aqua>: atlas emoji/icon preset\n",
                                                List.of(StyleAction.of(StyleType.ATLAS_PRESET, ""))
                                        )
                                )
                        ),
                        entry("embellish-chat.command_argument", List.of())
                )),
                new LinkedHashMap<>(Map.ofEntries(
                        entry("embellish-chat.mention", List.of(
                                MentionRule.of(
                                        "@here()",
                                        "<blue><b>Pattern</b></blue>: @here\n<dark_aqua><b>Comment</b></dark_aqua>: mention players within 64 blocks\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.INSIDE, "64")),
                                        List.of(
                                                StyleAction.of(StyleType.BOLD, ""),
                                                StyleAction.of(StyleType.COLOR_PRESET, "light purple")
                                        )
                                ),
                                MentionRule.of(
                                        "@everyone()",
                                        "<blue><b>Pattern</b></blue>: @everyone\n<dark_aqua><b>Comment</b></dark_aqua>: mention all online players\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.EVERYONE, "")),
                                        List.of(
                                                StyleAction.of(StyleType.BOLD, ""),
                                                StyleAction.of(StyleType.COLOR_PRESET, "light purple")
                                        )
                                ),
                                MentionRule.of(
                                        "@team\\((.+?)\\)",
                                        "<blue><b>Pattern</b></blue>: @team(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given scoreboard team\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.TEAM, "")),
                                        List.of(StyleAction.of(StyleType.BOLD, ""))
                                ),
                                MentionRule.of(
                                        "@group\\((.+?)\\)",
                                        "<blue><b>Pattern</b></blue>: @group(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given LuckPerms group\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.LUCK_PERMS_GROUP, "")),
                                        List.of(
                                                StyleAction.of(StyleType.BOLD, ""),
                                                StyleAction.of(StyleType.COLOR_PRESET, "light purple")
                                        )
                                ),
                                MentionRule.of(
                                        "@world\\((.+?)\\)",
                                        "<blue><b>Pattern</b></blue>: @world(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given world\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.WORLD, "")),
                                        List.of(
                                                StyleAction.of(StyleType.BOLD, ""),
                                                StyleAction.of(StyleType.COLOR_PRESET, "light purple")
                                        )
                                ),
                                MentionRule.of(
                                        "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
                                        "<blue><b>Pattern</b></blue>: @Player\n<dark_aqua><b>Comment</b></dark_aqua>: mention a specific player\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.PLAYER, "")),
                                        List.of(StyleAction.of(StyleType.BOLD, ""))
                                )
                        )),
                        entry("embellish-chat.example", List.of(
                                MentionRule.of(
                                        "@admin()",
                                        "<blue><b>Pattern</b></blue>: @admin\n<dark_aqua><b>Comment</b></dark_aqua>: Alerts admins on Discord and adds a click-to-teleport action.\n<red><b>Note</b></red>: This mention is placed behind the default rules, so it will not work as-is. To activate it, move it to the top of the embellish-chat.mention list.\n",
                                        "%player:displayname% mentioned you",
                                        Sound.of("minecraft:entity.experience_orb.pickup", SoundCategory.UI, 1, 1.75f),
                                        0,
                                        false,
                                        List.of(MentionAction.of(MentionType.LUCK_PERMS_GROUP, "admin")),
                                        List.of(
                                                StyleAction.of(StyleType.BOLD, ""),
                                                StyleAction.of(StyleType.COLOR_GRADIENT, "#FF5555#C77DFF"),
                                                StyleAction.of(StyleType.CLICK_COMMAND_RUN, "execute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%"),
                                                StyleAction.of(StyleType.DISCORD_JSON, "{\"embeds\":[{\"title\":\"%player:name_unformatted% mentioned admins\",\"color\":16753920,\"description\":\"TP command\\n```mcfunction\\nexecute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%\\n```\",\"fields\":[{\"name\":\"Content\",\"value\":\"%embellish-chat:content%\",\"inline\":false},{\"name\":\"UUID\",\"value\":\"`%player:uuid%`\",\"inline\":false},{\"name\":\"Player\",\"value\":\"`%player:name_unformatted%`\",\"inline\":true},{\"name\":\"Ping\",\"value\":\"`%player:ping% ms`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Position\",\"value\":\"`%player:pos_x% %player:pos_y% %player:pos_z%`\",\"inline\":true},{\"name\":\"World\",\"value\":\"`%world:id%`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Server\",\"value\":\"`%server:name%`\",\"inline\":true},{\"name\":\"Time\",\"value\":\"`%server:time%`\",\"inline\":true},{\"name\":\"Status\",\"value\":\"`TPS:%server:tps%` `MSPT:%server:mspt%`\",\"inline\":true}]}]}")
                                        )
                                )
                        ))
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
                new LinkedHashMap<>(),

                //setting
                ",",
                "yyyy-MM-dd HH:mm:ss",
                "ec",
                new Color(0x0000EE),
                new Color(0xFF55FF),
                true,
                true,
                false,

                //player list
                new HashSet<>(),
                new HashSet<>()
        );
    }
}