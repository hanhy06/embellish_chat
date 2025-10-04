package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Metadata;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
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

    private Config config = null;
    private int defaultChatColor = 0;
    private StyleSpriteSource defaultChatFont = null;
    private HashMap<String,Integer> defaultColorPreset = null;

    public void updateConfig(Config config){
        this.config = config;
        this.defaultChatColor = config.defaultChatColor();
        this.defaultChatFont = new StyleSpriteSource.Font(
                Identifier.tryParse(config.defaultChatFont())
        );
        this.defaultColorPreset = config.defaultColorPreset();
    }

    public MutableText applyStyles(MutableText text, List<Receiver> receivers){
        if (text == null || text.getString().isBlank()) return text;

        MutableText result = text;

        result = applyDefaultColor(result);
        applyDefaultFont(result);

        if (receivers != null && !receivers.isEmpty()) {
            result = applyMention(result, receivers);
        }

        if (config.fontEnabled()){
            result = applyPattern(FONT,result,this::applyFont);
        }

        if (config.coloringEnabled()) {
            result = applyPattern(COLOR, result, this::applyColor);
        }

        if (config.openUriEnabled()){
            result = applyPattern(OPEN_URI,result,this::applyOpenURI);
        }

        if (config.markdownEnabled()) {
            result = applyMarkdown(result);
        }

        result = removeEscapeSlashes(result);
        return Metadata.metadata(result);
    }

    private MutableText applyDefaultColor(MutableText text) {
        if (defaultChatColor > 0) {
            text.styled(style -> style.withColor(defaultChatColor));
            return text;
        }
        if (defaultChatColor < 0) return applyRainbow(text);
        return text;
    }

    private void applyDefaultFont(MutableText text) {
        if (defaultChatFont != null) {
            text.styled(style -> style.withFont(defaultChatFont));
        }
    }

    private MutableText applyMarkdown(MutableText text) {
        MutableText result = text;

        result = applyPattern(BOLD,          result, Style.EMPTY.withBold(true));
        result = applyPattern(UNDERLINE,     result, Style.EMPTY.withUnderline(true));
        result = applyPattern(ITALIC,        result, Style.EMPTY.withItalic(true));
        result = applyPattern(STRIKETHROUGH, result, Style.EMPTY.withStrikethrough(true));
        result = applyPattern(OBFUSCATED,    result, Style.EMPTY.withObfuscated(true));

        return result;
    }

    private MutableText applyPattern(Pattern pattern, MutableText text, Style style){
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

    private MutableText applyPattern(Pattern pattern, MutableText text, BiFunction<MutableText,String,MutableText> function){
        String str = text.getString();
        Matcher matcher = pattern.matcher(str);

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            MutableText styledText = function.apply(
                    substring(text, matcher.start(1), matcher.end(1)),
                    matcher.group(2)
            );

            result.append(substring(text, lastEnd, matcher.start()));
            result.append(
                    styledText
            );
            lastEnd = matcher.end();
        }

        result.append(substring(text, lastEnd, str.length()));

        return result;
    }

    private MutableText applyColor(MutableText text, String strColor){
        if (strColor.charAt(0) == '#'){
            int color = Color.decode(strColor).getRGB();
            return text.styled(style -> style.withColor(color));
        } else if (strColor.equals("rainbow")) {
            return applyRainbow(text);
        } else {
            int color = defaultColorPreset.getOrDefault(strColor,0xFFFFFF);
            return text.styled(style -> style.withColor(color));
        }
    }

    private MutableText applyFont(MutableText text, String strFont){
        return text.styled(style -> style.withFont(
                new StyleSpriteSource.Font(Identifier.tryParse(strFont))
        ));
    }

    private MutableText applyOpenURI(MutableText text, String strUri){
        try {
            URI uri = URI.create(strUri);
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return text.styled(style -> style
                    .withClickEvent(clickEvent)
                    .withColor(0x0000FF)
            );
        }catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", strUri);
            return text;
        }
    }

    private MutableText applyRainbow(MutableText text){
        MutableText result = Text.empty();

        int length = text.getString().length();

        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, 0.7f, 1f);
            result.append(
                    substring(text, i, i+1).styled(
                            style -> style.withColor(rgb)
                    )
            );
        }

        return result;
    }

    private MutableText applyMention(MutableText text, List<Receiver> receivers){
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