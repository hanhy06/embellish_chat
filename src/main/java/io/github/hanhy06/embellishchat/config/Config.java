package io.github.hanhy06.embellishchat.config;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleRule;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;

import java.awt.*;
import java.util.*;
import java.util.List;

public record Config(
        String version,

        //rules
        LinkedHashMap<String,List<StyleRule>> style_rules,
        LinkedHashMap<String,List<MentionRule>> mention_rules,

        //preset
        HashMap<String, Color> color,
        HashMap<String, AtlasSprite> atlas,
        HashSet<String> whitelist,
        LinkedHashMap<String, MutableComponent> prefix,

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
    public static Config createDefault() {
        return new Config(
                FabricLoader.getInstance()
                        .getModContainer(EmbellishChat.MOD_ID)
                        .orElseThrow()
                        .getMetadata()
                        .getVersion()
                        .getFriendlyString(),

                createDefaultStyleRules(),
                createDefaultMentionRules(),

                createDefaultColors(),
                createDefaultAtlas(),
                new HashSet<>(),
                new LinkedHashMap<>(),

                ",",
                "yyyy-MM-dd HH:mm:ss",
                "ec",
                new Color(0x0000EE),
                new Color(0xFF55FF),
                true,
                true,
                false,

                new HashSet<>(),
                new HashSet<>()
        );
    }

    private static LinkedHashMap<String, List<StyleRule>> createDefaultStyleRules() {
        LinkedHashMap<String, List<StyleRule>> styleRules = new LinkedHashMap<>();

        styleRules.put("embellish-chat.chat", List.of(
                StyleRule.of(
                        "\\[([^\\]]+?)]\\((https://.*?)\\)",
                        "<blue><b>Pattern</b></blue>: [Text](url)\n<dark_aqua><b>Comment</b></dark_aqua>: clickable link with custom text\n",
                        List.of(StyleAction.of(StyleType.URL, ""))
                ),
                StyleRule.of(
                        "((https://\\S+))",
                        "<blue><b>Pattern</b></blue>: url\n<dark_aqua><b>Comment</b></dark_aqua>: auto-detect clickable link\n",
                        List.of(StyleAction.of(StyleType.URL, ""))
                ),
                StyleRule.of(
                        "\\[([^\\]]+?)]\\{(.*?)}",
                        "<blue><b>Pattern</b></blue>: [Text]{font}\n<dark_aqua><b>Comment</b></dark_aqua>: apply custom font to text\n",
                        List.of(StyleAction.of(StyleType.FONT, ""))
                ),
                StyleRule.of(
                        "\\[([^\\]]+?)]<(#.{6})>",
                        "<blue><b>Pattern</b></blue>: [Text]<#RRGGBB>\n<dark_aqua><b>Comment</b></dark_aqua>: apply hex color\n",
                        List.of(StyleAction.of(StyleType.COLOR_HEX, ""))
                ),
                StyleRule.of(
                        "\\[([^\\]]+?)]<(#.{6,})>",
                        "<blue><b>Pattern</b></blue>: [Text]<#color1#color2...>\n<dark_aqua><b>Comment</b></dark_aqua>: apply gradient color\n",
                        List.of(StyleAction.of(StyleType.COLOR_GRADIENT, ""))
                ),
                StyleRule.of(
                        "\\[([^\\]]+?)]<([a-z\\s]+?)>",
                        "<blue><b>Pattern</b></blue>: [Text]<preset>\n<dark_aqua><b>Comment</b></dark_aqua>: apply preset color name\n",
                        List.of(StyleAction.of(StyleType.COLOR_PRESET, ""))
                ),
                StyleRule.of(
                        "\\*\\*(.+?)\\*\\*()",
                        "<blue><b>Pattern</b></blue>: **Text**\n<dark_aqua><b>Comment</b></dark_aqua>: bold formatting\n",
                        List.of(StyleAction.of(StyleType.BOLD, ""))
                ),
                StyleRule.of(
                        "__(.+?)__()",
                        "<blue><b>Pattern</b></blue>: __Text__\n<dark_aqua><b>Comment</b></dark_aqua>: underline formatting\n",
                        List.of(StyleAction.of(StyleType.UNDERLINE, ""))
                ),
                StyleRule.of(
                        "_(.+?)_()",
                        "<blue><b>Pattern</b></blue>: _Text_\n<dark_aqua><b>Comment</b></dark_aqua>: italic formatting\n",
                        List.of(StyleAction.of(StyleType.ITALIC, ""))
                ),
                StyleRule.of(
                        "~~(.+?)~~()",
                        "<blue><b>Pattern</b></blue>: ~~Text~~\n<dark_aqua><b>Comment</b></dark_aqua>: strikethrough formatting\n",
                        List.of(StyleAction.of(StyleType.STRIKETHROUGH, ""))
                ),
                StyleRule.of(
                        "\\|\\|(.+?)\\|\\|()",
                        "<blue><b>Pattern</b></blue>: ||Text||\n<dark_aqua><b>Comment</b></dark_aqua>: obfuscated text\n",
                        List.of(StyleAction.of(StyleType.OBFUSCATED, ""))
                ),
                StyleRule.of(
                        "\\[([^\\]]+?)]<(RAINBOW)>",
                        "<blue><b>Pattern</b></blue>: [Text]<RAINBOW>\n<dark_aqua><b>Comment</b></dark_aqua>: rainbow color\n",
                        List.of(StyleAction.of(StyleType.COLOR_RAINBOW, "0.7"))
                ),
                StyleRule.of(
                        "(.+)()",
                        "<blue><b>Pattern</b></blue>: any text\n<dark_aqua><b>Comment</b></dark_aqua>: metadata capture layer\n",
                        List.of(StyleAction.of(StyleType.METADATA, ""))
                ),
                StyleRule.of(
                        "(\\[i\\])()",
                        "<blue><b>Pattern</b></blue>: [i]\n<dark_aqua><b>Comment</b></dark_aqua>: show held item\n",
                        List.of(StyleAction.of(StyleType.SHOW_ITEM, ""))
                ),
                StyleRule.of(
                        "(\\[inv\\])()",
                        "<blue><b>Pattern</b></blue>: [inv]\n<dark_aqua><b>Comment</b></dark_aqua>: show player inventory\n",
                        List.of(StyleAction.of(StyleType.SHOW_INVENTORY, ""))
                ),
                StyleRule.of(
                        "(\\[end\\])()",
                        "<blue><b>Pattern</b></blue>: [end]\n<dark_aqua><b>Comment</b></dark_aqua>: show ender chest contents\n",
                        List.of(StyleAction.of(StyleType.SHOW_ENDER_CHEST, ""))
                ),
                StyleRule.of(
                        "(:(.+?):)",
                        "<blue><b>Pattern</b></blue>: :icon:\n<dark_aqua><b>Comment</b></dark_aqua>: atlas emoji/icon preset\n",
                        List.of(StyleAction.of(StyleType.ATLAS_PRESET, ""))
                )
        ));

        return styleRules;
    }

    private static LinkedHashMap<String, List<MentionRule>> createDefaultMentionRules() {
        LinkedHashMap<String, List<MentionRule>> mentionRules = new LinkedHashMap<>();

        mentionRules.put("embellish-chat.mention", List.of(
                MentionRule.of(
                        "@here()",
                        "<blue><b>Pattern</b></blue>: @here\n<dark_aqua><b>Comment</b></dark_aqua>: mention players within 64 blocks\n",
                        "%player:displayname% mentioned you",
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
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
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
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
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
                        0,
                        false,
                        List.of(MentionAction.of(MentionType.TEAM, "")),
                        List.of(StyleAction.of(StyleType.BOLD, ""))
                ),
                MentionRule.of(
                        "@group\\((.+?)\\)",
                        "<blue><b>Pattern</b></blue>: @group(name)\n<dark_aqua><b>Comment</b></dark_aqua>: mention players in the given LuckPerms group\n",
                        "%player:displayname% mentioned you",
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
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
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
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
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
                        0,
                        false,
                        List.of(MentionAction.of(MentionType.PLAYER, "")),
                        List.of(StyleAction.of(StyleType.BOLD, ""))
                )
        ));

        mentionRules.put("embellish-chat.example", List.of(
                MentionRule.of(
                        "@admin()",
                        "<blue><b>Pattern</b></blue>: @admin\n<dark_aqua><b>Comment</b></dark_aqua>: Alerts admins on Discord and adds a click-to-teleport action.\n<red><b>Note</b></red>: This mention is placed behind the default rules, so it will not work as-is. To activate it, move it to the top of the embellish-chat.mention list.\n",
                        "%player:displayname% mentioned you",
                        Sound.of("minecraft:entity.experience_orb.pickup", SoundSource.UI, 1, 1.75f),
                        0,
                        false,
                        List.of(MentionAction.of(MentionType.LUCK_PERMS_GROUP, "admin")),
                        List.of(
                                StyleAction.of(StyleType.BOLD, ""),
                                StyleAction.of(StyleType.COLOR_GRADIENT, "#FF5555#C77DFF"),
                                StyleAction.of(StyleType.CLICK_COMMAND_RUN, "execute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%"),
                                StyleAction.of(
                                        StyleType.DISCORD_JSON,
                                        "your webhook;{\"embeds\":[{\"title\":\"%player:name_unformatted% mentioned admins\",\"color\":16753920,\"description\":\"TP command\\n```mcfunction\\nexecute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%\\n```\",\"fields\":[{\"name\":\"Content\",\"value\":\"%embellish-chat:content%\",\"inline\":false},{\"name\":\"UUID\",\"value\":\"`%player:uuid%`\",\"inline\":false},{\"name\":\"Player\",\"value\":\"`%player:name_unformatted%`\",\"inline\":true},{\"name\":\"Ping\",\"value\":\"`%player:ping% ms`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Position\",\"value\":\"`%player:pos_x% %player:pos_y% %player:pos_z%`\",\"inline\":true},{\"name\":\"World\",\"value\":\"`%world:id%`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Server\",\"value\":\"`%server:name%`\",\"inline\":true},{\"name\":\"Time\",\"value\":\"`%server:time%`\",\"inline\":true},{\"name\":\"Status\",\"value\":\"`TPS:%server:tps%` `MSPT:%server:mspt%`\",\"inline\":true}]}]}"
                                )
                        )
                )
        ));

        return mentionRules;
    }

    private static HashMap<String, Color> createDefaultColors() {
        HashMap<String, Color> colors = new HashMap<>();

        colors.put("black", new Color(0x000000));
        colors.put("dark blue", new Color(0x0000AA));
        colors.put("dark green", new Color(0x00AA00));
        colors.put("dark aqua", new Color(0x00AAAA));
        colors.put("dark red", new Color(0xAA0000));
        colors.put("dark purple", new Color(0xAA00AA));
        colors.put("gold", new Color(0xFFAA00));
        colors.put("gray", new Color(0xAAAAAA));
        colors.put("dark gray", new Color(0x555555));
        colors.put("blue", new Color(0x5555FF));
        colors.put("green", new Color(0x55FF55));
        colors.put("aqua", new Color(0x55FFFF));
        colors.put("red", new Color(0xFF5555));
        colors.put("light purple", new Color(0xFF55FF));
        colors.put("yellow", new Color(0xFFFF55));
        colors.put("white", new Color(0xFFFFFF));

        return colors;
    }

    private static HashMap<String, AtlasSprite> createDefaultAtlas() {
        HashMap<String, AtlasSprite> atlas = new HashMap<>();

        atlas.put("fire", new AtlasSprite(
                Identifier.parse("minecraft:blocks"),
                Identifier.parse("minecraft:block/campfire_fire"))
        );
        atlas.put("food", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/cooked_beef"))
        );
        atlas.put("hunger", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:hud/food_half"))
        );
        atlas.put("heart", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:hud/heart/full"))
        );
        atlas.put("love", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:mob_effect/health_boost"))
        );
        atlas.put("yes", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:container/beacon/confirm"))
        );
        atlas.put("no", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:container/beacon/cancel"))
        );
        atlas.put("move", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:mob_effect/wind_charged"))
        );
        atlas.put("emerald", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/emerald"))
        );
        atlas.put("diamond", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/diamond"))
        );
        atlas.put("star", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/nether_star"))
        );
        atlas.put("time", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/clock_54"))
        );
        atlas.put("note", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/paper"))
        );
        atlas.put("totem", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/totem_of_undying"))
        );
        atlas.put("music", new AtlasSprite(
                Identifier.parse("minecraft:particles"),
                Identifier.parse("minecraft:note"))
        );
        atlas.put("mine", new AtlasSprite(
                Identifier.parse("minecraft:items"),
                Identifier.parse("minecraft:item/diamond_pickaxe"))
        );
        atlas.put("luck", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:mob_effect/luck"))
        );
        atlas.put("poison", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:mob_effect/poison"))
        );
        atlas.put("fight", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:mob_effect/raid_omen"))
        );
        atlas.put("world", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:icon/link"))
        );
        atlas.put("news", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:icon/news"))
        );
        atlas.put("search", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:icon/search"))
        );
        atlas.put("tv", new AtlasSprite(
                Identifier.parse("minecraft:gui"),
                Identifier.parse("minecraft:icon/video_link"))
        );

        return atlas;
    }

}
