package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Metadata;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyledTextProcessor {
    private static final Pattern BOLD = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static final Pattern UNDERLINE = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static final Pattern ITALIC = Pattern.compile("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)");
    private static final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static final Pattern OBFUSCATED = Pattern.compile("(?<!\\\\)\\|\\|(.+?)\\|\\|");
    private static final Pattern COLOR = Pattern.compile("(?<!\\\\)\\[(.+?)]<([^>]+)>");
    private static final Pattern OPEN_URI = Pattern.compile("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+)\\)");
    private static final Pattern FONT = Pattern.compile("(?<!\\\\)\\[(.+?)]\\{([^}]+)}");

    public static MutableText applyStyles(Config config, MutableText text, List<Receiver> receivers){
        if (text == null || text.getString().isBlank()) return text;

        MutableText result = text;

        result = applyDefaultColor(config, result);
        applyDefaultFont(config, result);

        if (receivers != null && !receivers.isEmpty()) {
            result = applyMention(result, receivers);
        }

        if (config.fontEnabled()){
            result = applyPattern(FONT,result,StyledTextProcessor::withFont);
        }

        if (config.coloringEnabled()) {
            result = applyPattern(COLOR, result, StyledTextProcessor::withColor);
        }

        if (config.openUriEnabled()){
            result = applyPattern(OPEN_URI,result,StyledTextProcessor::withOpenURI);
        }

        if (config.markdownEnabled()) {
            result = applyMarkdown(result);
        }

        result = removeEscapeSlashes(result);
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
            text.fillStyle(Style.EMPTY.withFont(Identifier.tryParse(font)));
        }
    }

    private static MutableText applyMarkdown(MutableText text) {
        MutableText result = text;

        result = applyPattern(BOLD,          result, Style.EMPTY.withBold(true));
        result = applyPattern(UNDERLINE,     result, Style.EMPTY.withUnderline(true));
        result = applyPattern(ITALIC,        result, Style.EMPTY.withItalic(true));
        result = applyPattern(STRIKETHROUGH, result, Style.EMPTY.withStrikethrough(true));
        result = applyPattern(OBFUSCATED,    result, Style.EMPTY.withObfuscated(true));

        return result;
    }

    private static MutableText applyPattern(Pattern pattern, MutableText text, Style style){
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

    private static MutableText applyPattern(Pattern pattern, MutableText text, Function<String,Style> function){
        String str = text.getString();
        Matcher matcher = pattern.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            Style style = function.apply(matcher.group(2));

            result.append(substring(text, lastEnd, matcher.start()));
            result.append(
                    substring(text, matcher.start(1), matcher.end(1)).fillStyle(style)
            );
            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));

        return result;
    }

    private static Style withColor(String strColor){
        if (strColor.charAt(0) == '#'){
            int color = Color.decode(strColor).getRGB();
            return Style.EMPTY.withColor(color);
        }else {
            int color = ConfigManager.getConfig().defaultColorPreset().getOrDefault(strColor,0xFFFFFF);
            return Style.EMPTY.withColor(color);
        }
    }

    private static Style withFont(String fontId){
        return Style.EMPTY.withFont(
                Identifier.tryParse(fontId)
        );
    }

    private static Style withOpenURI(String strUri){
        try {
            URI uri = URI.create(strUri);
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return Style.EMPTY.withClickEvent(clickEvent).withColor(0x0000FF);
        }catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", strUri);
            return Style.EMPTY;
        }
    }

    private static MutableText applyMention(MutableText text, List<Receiver> receivers){
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

    private static MutableText applyRainbow(MutableText text){
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