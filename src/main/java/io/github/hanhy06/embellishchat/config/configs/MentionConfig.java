package io.github.hanhy06.embellishchat.config.configs;

import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import net.minecraft.sound.SoundCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public record MentionConfig(
        LinkedHashMap<String, List<MentionRule>> MENTION_RULES
) {
    public static MentionConfig createDefault(){
        return new MentionConfig(
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
                ))
        );
    }
}
