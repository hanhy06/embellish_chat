package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Metadata;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyledTextProcessor {
    private static final int LINK_COLOR = 0x0000EE;

    private static final Pattern BOLD          = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static final Pattern UNDERLINE     = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static final Pattern ITALIC        = Pattern.compile("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)");
    private static final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static final Pattern OBFUSCATED    = Pattern.compile("(?<!\\\\)\\|\\|(.+?)\\|\\|");
    private static final Pattern BRACKET = Pattern.compile(
            "(?<!\\\\)\\[(.+?)](?:\\((https://[^\\s)]+)\\)|\\{(.+?)}|<(#[0-9A-Fa-f]{6})>)"
    );

    public static MutableText applyStyles(Config config, MutableText text, List<Receiver> receivers) {
        if (text == null || text.getString().isBlank()) return text;

        MutableText result = text;

        result = applyDefaultColor(config, result);
        applyDefaultFont(config, result);

        if (config.mentionEnabled() && receivers != null && !receivers.isEmpty()) {
            result = applyMention(result, receivers);
        }

        if (config.markdownEnabled()) {
            result = applyMarkdown(result);
            result = applyStyledBracketed(config, result);
        }

        return Metadata.metadata(result);
    }

    private static MutableText applyDefaultColor(Config config, MutableText text) {
        int color = config.defaultChatColor();
        if (color > 0) {
            text.fillStyle(Style.EMPTY.withColor(color));
            return text;
        }
        if (color < 0) return applyRainbow(text);
        return text;
    }

    private static void applyDefaultFont(Config config, MutableText text) {
        String font = config.defaultChatFont();
        if (!font.isEmpty()) {
            text.fillStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.tryParse(font))));
        }
    }

    private static MutableText applyMarkdown(MutableText text) {
        MutableText result = text;

        result = applyStyledPattern(BOLD,          result, Style.EMPTY.withBold(true));
        result = applyStyledPattern(UNDERLINE,     result, Style.EMPTY.withUnderline(true));
        result = applyStyledPattern(ITALIC,        result, Style.EMPTY.withItalic(true));
        result = applyStyledPattern(STRIKETHROUGH, result, Style.EMPTY.withStrikethrough(true));
        result = applyStyledPattern(OBFUSCATED,    result, Style.EMPTY.withObfuscated(true));
        result = removeEscapeSlashes(result);

        return result;
    }

    private static MutableText applyStyledPattern(Pattern pattern, MutableText source, Style style) {
        String str = source.getString();
        Matcher matcher = pattern.matcher(str);

        MutableText out = Text.empty();
        int last = 0;

        while (matcher.find()) {
            out.append(substring(source, last, matcher.start()));
            out.append(substring(source, matcher.start(1), matcher.end(1)).fillStyle(style));
            last = matcher.end();
        }
        out.append(substring(source, last, str.length()));
        return out;
    }

    private static MutableText applyStyledBracketed(Config config, MutableText source) {
        String str = source.getString();
        Matcher matcher = BRACKET.matcher(str);

        MutableText out = Text.empty();
        int last = 0;

        while (matcher.find()) {
            out.append(substring(source, last, matcher.start()));

            char tail = str.charAt(matcher.end() - 1);
            Style style = Style.EMPTY;

            if (tail == ')' && config.openUriEnabled()) {
                style = withUrl(matcher.group(2));
            } else if (tail == '}' && config.fontEnabled()) {
                style = withFont(matcher.group(3));
            } else if (tail == '>' && config.coloringEnabled()) {
                style = withColor(matcher.group(4));
            }

            out.append(substring(source, matcher.start(1), matcher.end(1)).fillStyle(style));
            last = matcher.end();
        }
        out.append(substring(source, last, str.length()));
        return out;
    }

    private static Style withColor(String hex) {
        int rgb = Color.decode(hex).getRGB();
        return Style.EMPTY.withColor(rgb);
    }

    private static Style withFont(String fontId) {
        return Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.tryParse(fontId)));
    }

    private static Style withUrl(String url) {
        try {
            URI uri = URI.create(url);
            ClickEvent click = new ClickEvent.OpenUrl(uri);
            return Style.EMPTY.withClickEvent(click).withColor(LINK_COLOR);
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", url);
            return Style.EMPTY;
        }
    }

    private static MutableText applyMention(MutableText source, List<Receiver> receivers) {
        MutableText out = Text.empty();
        int last = 0;

        for (Receiver r : receivers) {
            out.append(substring(source, last, r.begin()));
            out.append(substring(source, r.begin(), r.end())
                    .fillStyle(Style.EMPTY.withColor(r.teamColor()).withBold(true)));
            last = r.end();
        }
        out.append(substring(source, last, source.getString().length()));
        return out;
    }

    private static MutableText applyRainbow(MutableText source) {
        MutableText out = Text.empty();
        int n = source.getString().length();

        for (int i = 0; i < n; i++) {
            float hue = (float) i / n;
            int rgb = Color.HSBtoRGB(hue, 0.7f, 1f);
            out.append(substring(source, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return out;
    }

    private static MutableText removeEscapeSlashes(MutableText text) {
        MutableText result = Text.empty();
        final int[] offset = { 0 };

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                String replaced = content.replaceAll("\\\\([*_~#\\\\])", "$1");
                result.append(Text.literal(replaced).setStyle(style));

                offset[0] += content.length();
                return Optional.empty();
            }
        }, Style.EMPTY);

        return result;
    }


    private static MutableText substring(Text text, int beginIndex, int endIndex) {
        if (beginIndex >= endIndex || text == null) {
            return Text.empty();
        }

        MutableText result = Text.empty();
        final int[] currentCharacterOffset = {0};

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                int contentStartOffset = currentCharacterOffset[0];
                int contentEndOffset = contentStartOffset + content.length();

                int effectiveStartIndexInFullText = Math.max(contentStartOffset, beginIndex);
                int effectiveEndIndexInFullText = Math.min(contentEndOffset, endIndex);

                if (effectiveStartIndexInFullText < effectiveEndIndexInFullText) {
                    int subStartIndexInContentPiece = effectiveStartIndexInFullText - contentStartOffset;
                    int subEndIndexInContentPiece = effectiveEndIndexInFullText - contentStartOffset;

                    String subContent = content.substring(subStartIndexInContentPiece, subEndIndexInContentPiece);
                    result.append(Text.literal(subContent).setStyle(style));
                }
                currentCharacterOffset[0] = contentEndOffset;
                return Optional.empty();
            }
        }, Style.EMPTY);

        return result;
    }
}
