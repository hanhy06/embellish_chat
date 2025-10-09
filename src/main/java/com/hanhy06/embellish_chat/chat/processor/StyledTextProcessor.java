package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Metadata;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
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
    private static final Pattern COLOR = Pattern.compile("(?<!\\\\)\\[(.+?)]<([^>]+?)>");
    private static final Pattern OPEN_URI = Pattern.compile("(?<![\\\\!])\\[(.+?)]\\((https://[^\\s)]+?)\\)");
    private static final Pattern FONT = Pattern.compile("(?<!\\\\)\\[(.+?)]\\{([^}]+?)}");

    private static final Pattern ESCAPES = Pattern.compile("\\\\([*_~#\\\\])");

    private static final int URL_COLOR = 0x0000EE;

    private Config config = null;
    private int defaultChatColor = 0;
    private StyleSpriteSource defaultChatFont = null;
    private HashMap<String, Integer> defaultColorPreset = null;

    public void updateConfig(Config config) {
        this.config = config;
        this.defaultChatColor = config.defaultChatColor();
        this.defaultColorPreset = config.defaultColorPreset();
        if (!config.defaultChatFont().isEmpty()){
            this.defaultChatFont = new StyleSpriteSource.Font(Identifier.tryParse(config.defaultChatFont()));
        } else {
            this.defaultChatFont = null;
        }
    }

    public MutableText applyStyles(MutableText text, List<Receiver> receivers) {
        if (text == null || text.getString().isBlank()) return text;

        MutableText result = text;

        result = applyDefaultColor(result);
        result = applyDefaultFont(result);

        if (receivers != null && !receivers.isEmpty()) {
            result = applyMention(result, receivers);
        }

        if (config.markdownEnabled()) {
            result = applyMarkdown(result);
        }

        if (config.fontEnabled()) {
            result = applyPattern(FONT, result, this::applyFont);
        }

        if (config.openUriEnabled()) {
            result = applyPattern(OPEN_URI, result, this::applyOpenURI);
        }

        if (config.coloringEnabled()) {
            result = applyPattern(COLOR, result, this::applyColor);
        }

        result = removeEscapeSlashes(result);
        return Metadata.metadata(result);
    }

    private MutableText applyDefaultColor(MutableText text) {
        if (defaultChatColor > 0) {
            return text.fillStyle(Style.EMPTY.withColor(defaultChatColor));
        } else if (defaultChatColor < 0) {
            return applyRainbow(text);
        } else {
            return text;
        }
    }

    private MutableText applyDefaultFont(MutableText text) {
        if (defaultChatFont != null) {
            return text.fillStyle(Style.EMPTY.withFont(defaultChatFont));
        }
        return text;
    }

    private MutableText applyMarkdown(MutableText text) {
        MutableText result = text;
        result = applyPattern(BOLD, result, Style.EMPTY.withBold(true));
        result = applyPattern(UNDERLINE, result, Style.EMPTY.withUnderline(true));
        result = applyPattern(ITALIC, result, Style.EMPTY.withItalic(true));
        result = applyPattern(STRIKETHROUGH, result, Style.EMPTY.withStrikethrough(true));
        result = applyPattern(OBFUSCATED, result, Style.EMPTY.withObfuscated(true));
        return result;
    }

    private MutableText applyPattern(Pattern pattern, MutableText text, Style style) {
        Runs runs = flatten(text);
        Matcher matcher = pattern.matcher(runs.full);
        if (!matcher.find()) return text;

        MutableText result = Text.empty();
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));
            result.append(slice(runs, matcher.start(1), matcher.end(1)).fillStyle(style));
            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full.length()));

        return result;
    }

    private MutableText applyPattern(Pattern pattern, MutableText text, BiFunction<MutableText, String, MutableText> function) {
        Runs runs = flatten(text);
        Matcher matcher = pattern.matcher(runs.full);
        if (!matcher.find()) return text;

        MutableText result = Text.empty();
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));
            result.append(
                    function.apply(
                            slice(runs, matcher.start(1), matcher.end(1)),
                            matcher.group(2)
                    )
            );
            lastEnd = matcher.end();
        } while (matcher.find());

        result.append(slice(runs, lastEnd, runs.full.length()));
        return result;
    }

    private MutableText applyColor(MutableText text, String strColor) {
        Integer preset = defaultColorPreset.get(strColor);
        if (preset != null) {
            return text.fillStyle(Style.EMPTY.withColor(preset));
        } else if (!strColor.isEmpty() && strColor.charAt(0) == '#') {
            int color = Color.decode(strColor).getRGB();
            return text.fillStyle(Style.EMPTY.withColor(color));
        } else if (strColor.equals("rainbow") && config.rainbowEnabled()) {
            return applyRainbow(text);
        }
        return text;
    }

    private MutableText applyFont(MutableText text, String strFont) {
        return text.fillStyle(Style.EMPTY.withFont(new StyleSpriteSource.Font(Identifier.tryParse(strFont))));
    }

    private MutableText applyOpenURI(MutableText text, String strUri) {
        try {
            URI uri = URI.create(strUri);
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withColor(URL_COLOR));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", strUri);
            return text;
        }
    }

    private MutableText applyRainbow(MutableText text) {
        Runs runs = flatten(text);
        String string = runs.full;
        int length = string.length();
        if (length == 0) return text;

        MutableText out = Text.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, 0.7f, 1f);
            out.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return out;
    }

    private MutableText applyMention(MutableText text, List<Receiver> receivers) {
        Runs runs = flatten(text);
        MutableText result = Text.empty();
        int lastEnd = 0;
        for (Receiver receiver : receivers) {
            result.append(slice(runs, lastEnd, receiver.begin()));
            result.append(
                    slice(runs, receiver.begin(), receiver.end())
                            .fillStyle(Style.EMPTY.withColor(receiver.teamColor()).withBold(true))
            );
            lastEnd = receiver.end();
        }
        result.append(slice(runs, lastEnd, runs.full.length()));
        return result;
    }

    private static MutableText removeEscapeSlashes(MutableText text) {
        Runs runs = flatten(text);
        MutableText result = Text.empty();
        for (Run run : runs.runs) {
            String content = run.content;
            if (content.indexOf('\\') < 0) {
                result.append(Text.literal(content).setStyle(run.style));
            } else {
                String replaced = ESCAPES.matcher(content).replaceAll("$1");
                result.append(Text.literal(replaced).setStyle(run.style));
            }
        }
        return result;
    }

    record Run(
            int start,
            int end,
            Style style,
            String content
    ) {}

    record Runs(
            String full,
            List<Run> runs
    ) {}

    private static Runs flatten(Text text) {
        List<Run> list = new ArrayList<>();
        StringBuilder all = new StringBuilder();

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                int start = all.length();
                all.append(content);
                int end = all.length();
                list.add(new Run(start, end, style, content));
                return Optional.empty();
            }
        }, Style.EMPTY);

        return new Runs(all.toString(), list);
    }

    private static MutableText slice(Runs runs, int begin, int end) {
        MutableText out = Text.empty();
        for (Run run : runs.runs) {
            if (run.end <= begin) continue;
            if (run.start >= end) break;

            int startIndex = Math.max(begin, run.start) - run.start;
            int endIndex = Math.min(end, run.end) - run.start;
            out.append(Text.literal(run.content.substring(startIndex, endIndex)).setStyle(run.style));
        }
        return out;
    }
}
