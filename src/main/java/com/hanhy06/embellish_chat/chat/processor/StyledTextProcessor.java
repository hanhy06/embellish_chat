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
    private static final Pattern BOLD = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static final Pattern UNDERLINE = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static final Pattern ITALIC = Pattern.compile("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)");
    private static final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static final Pattern OBFUSCATED = Pattern.compile("(?<!\\\\)\\|\\|(.+?)\\|\\|");
    private static final Pattern COLOR = Pattern.compile("(?<!\\\\)(#[0-9A-Fa-f]{6})(.+?)#");
    private static final Pattern OPEN_URI = Pattern.compile("(?<![\\\\!])(\\[(.+?)])\\((https?://[^\\s)]+)\\)");
    private static final Pattern FONT = Pattern.compile("(?<!\\\\)(\\[(.+?)])\\{([^}]+)\\}");

    public static MutableText applyStyles(Config config, MutableText text, List<Receiver> receivers){
        if (text == null || text.getString().isBlank()) return text;

        MutableText result = text;

        int textColor = config.defaultChatColor();
        if (textColor > 0) {
            result.fillStyle(Style.EMPTY.withColor(textColor));
        } else if (textColor < 0) {
            result = applyStyledRainbow(result);
        }

        String font = config.defaultChatFont();
        if (!font.isEmpty()){
            result = result.fillStyle(Style.EMPTY.withFont(
                    new StyleSpriteSource.Font(Identifier.tryParse(font))
            ));
        }

        if (config.mentionEnabled()){
            result = applyStyledMention(result,receivers);
        }

        if (config.fontEnabled()) {
            result = applyStyledFont(result);
        }

        if (config.coloringEnabled()) {
            result = applyStyledColor(result);
        }

        if (config.markdownEnabled()){
            if (config.openUriEnabled()) result = applyStyledOpenURI(result);
            result = applyStyledPattern(BOLD,result,Style.EMPTY.withBold(true));
            result = applyStyledPattern(UNDERLINE,result,Style.EMPTY.withUnderline(true));
            result = applyStyledPattern(ITALIC,result,Style.EMPTY.withItalic(true));
            result = applyStyledPattern(STRIKETHROUGH,result,Style.EMPTY.withStrikethrough(true));
            result = applyStyledPattern(OBFUSCATED,result,Style.EMPTY.withObfuscated(true));
            result = removeEscapeSlashes(result);
        }

        return Metadata.metadata(result);
    }

    private static MutableText applyStyledPattern(Pattern pattern, MutableText text, Style style){
        String str = text.getString();
        Matcher matcher = pattern.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            result.append(substring(text, lastEnd, matcher.start()));
            result.append(
                    substring(text, matcher.start(1), matcher.end(1)).fillStyle(style)
            );
            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));

        return result;
    }

    private static MutableText applyStyledColor(MutableText text){
        String str = text.getString();
        Matcher matcher = COLOR.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            Color color = Color.decode(matcher.group(1));

            result.append(substring(text, lastEnd, matcher.start()));
            result.append(
                    substring(text, matcher.start(2), matcher.end(2))
                            .fillStyle(
                                    Style.EMPTY.withColor(color.getRGB())
                    )
            );
            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));

        return result;
    }

    private static MutableText applyStyledOpenURI(MutableText text) {
        String str = text.getString();
        Matcher matcher = OPEN_URI.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(substring(text, lastEnd, matcher.start()));

            URI uri;
            try {
                uri = URI.create(matcher.group(3));
            } catch (IllegalArgumentException e) {
                EmbellishChat.LOGGER.warn("Invalid URL address: {}", matcher.group(3));
                result.append(substring(text, matcher.start(), matcher.end()));
                lastEnd = matcher.end();
                continue;
            }

            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            result.append(
                    substring(text, matcher.start(2), matcher.end(2))
                            .fillStyle(Style.EMPTY
                                    .withClickEvent(clickEvent)
                                    .withColor(0x0000EE)
                            )
            );

            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));
        return result;
    }


    private static MutableText applyStyledFont(MutableText text) {
        String str = text.getString();
        Matcher matcher = FONT.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(substring(text, lastEnd, matcher.start()));
            result.append(
                    substring(text, matcher.start(2), matcher.end(2))
                            .fillStyle(Style.EMPTY
                                    .withFont(
                                            new StyleSpriteSource.Font(Identifier.tryParse(matcher.group(3)))
                                    )
                            )
            );
            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));
        return result;
    }

    private static MutableText applyStyledMention(MutableText text, List<Receiver> receivers){
        MutableText result = Text.empty();
        int lastEnd = 0;

        for (Receiver receiver: receivers){
            result.append(substring(text,lastEnd, receiver.begin()));
            result.append(
                    substring(text, receiver.begin(), receiver.end())
                            .fillStyle(
                                    Style.EMPTY.withColor(receiver.teamColor()).withBold(true)
                            )
            );
            lastEnd = receiver.end();
        }

        result.append(substring(text, lastEnd, text.getString().length()));

        return result;
    }

    private static MutableText applyStyledRainbow(MutableText text){
        MutableText result = Text.empty();

        int length = text.getString().length();

        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, 0.7f, 1f);
            result.append(
                    substring(text, i, i+1).fillStyle(
                            Style.EMPTY.withColor(rgb)
                    )
            );
        }

        return result;
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
