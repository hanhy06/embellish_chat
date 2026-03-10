package io.github.hanhy06.embellishchat.config.data;

import io.github.hanhy06.embellishchat.config.ConfigInterface;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public record Style(
        LinkedHashMap<String, List<StylingRule>> style_rules
) implements ConfigInterface {
    @Override
    public String getFileName() {
        return "styles.json";
    }

    @Override
    public ConfigInterface getDefault() {
        return new Style(
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
                ))
        );
    }
}
