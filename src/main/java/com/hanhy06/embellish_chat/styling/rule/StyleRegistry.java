package com.hanhy06.embellish_chat.styling.rule;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.mention.MentionTarget;
import com.hanhy06.embellish_chat.styling.util.Runs;
import com.hanhy06.embellish_chat.util.Timestamp;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.flatten;
import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.slice;
import static java.util.Map.entry;

public class StyleRegistry {
    private final Config config;
    private final HashMap<String,Integer> colorPreset;
    private final EnumMap<StyleType, BiFunction<MutableText,String,MutableText>> registers;

    public StyleRegistry(Config config){
        this.config = config;
        this.colorPreset = config.colorPreset();
        this.registers = new EnumMap<>(Map.ofEntries(
                entry(StyleType.METADATA, this::METADATA),
                entry(StyleType.COLOR_HEX, this::COLOR_HEX),
                entry(StyleType.COLOR_RAINBOW, this::COLOR_RAINBOW),
                entry(StyleType.COLOR_PRESET, this::COLOR_PRESET),
                entry(StyleType.COLOR_SHADOW, this::COLOR_SHADOW),
                entry(StyleType.FONT, this::FONT),
                entry(StyleType.URL, this::URL),
                entry(StyleType.BOLD, this::BOLD),
                entry(StyleType.ITALIC, this::ITALIC),
                entry(StyleType.UNDERLINE, this::UNDERLINE),
                entry(StyleType.STRIKETHROUGH, this::STRIKETHROUGH),
                entry(StyleType.OBFUSCATED, this::OBFUSCATED),
                entry(StyleType.REPLACE, this::REPLACE),
                entry(StyleType.MASK, this::MASK),
                entry(StyleType.UPPER, this::UPPER),
                entry(StyleType.LOWER, this::LOWER)
        ));
    }

    public BiFunction<MutableText,String,MutableText> get(StyleType styleType){
        return registers.get(styleType);
    }

    public MutableText METADATA(MutableText text, String option){
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(
                Timestamp.timeStamp() + "\nClick to copy to clipboard"
        ));

        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(text.getString());

        return text.fillStyle(
                Style.EMPTY.withHoverEvent(
                        hoverEvent
                ).withClickEvent(
                        clickEvent
                )
        );
    }

    public static MutableText MENTION(MutableText text, List<MentionTarget> targets){
        Runs runs = flatten(text);
        MutableText result = Text.empty();
        int lastEnd = 0;
        for (MentionTarget target : targets) {
            result.append(slice(runs, lastEnd, target.begin()));
            result.append(
                    slice(runs, target.begin(), target.end())
                            .fillStyle(Style.EMPTY.withColor(target.teamColor()).withBold(true))
            );
            lastEnd = target.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));
        return result;
    }

    public MutableText COLOR_HEX(MutableText text, String option){
        int color = Color.decode(option).getRGB();
        return text.fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_RAINBOW(MutableText text, String option){
        Runs runs = flatten(text);
        String string = runs.full();
        int length = string.length();
        float saturation = Float.parseFloat(option);
        if (length == 0) return text;

        MutableText result = Text.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, saturation, 1f);
            result.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return result;
    }

    public MutableText COLOR_PRESET(MutableText text, String option){
        int color = colorPreset.getOrDefault(option,0xFFFFFF);
        return text.fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_SHADOW(MutableText text, String  option){
        int color = Color.decode(option).getRGB();
        return text.fillStyle(Style.EMPTY.withShadowColor(color));
    }

    public MutableText FONT(MutableText text, String option){
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(option));
        return text.fillStyle(Style.EMPTY.withFont(font));
    }

    public MutableText URL(MutableText text, String option){
        try {
            URI uri = URI.create(option);
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withColor(config.urlColor()));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", option);
            return text;
        }
    }

    public MutableText BOLD(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withBold(true));
    }

    public MutableText ITALIC(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withItalic(true));
    }

    public MutableText UNDERLINE(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withUnderline(true));
    }

    public MutableText OBFUSCATED(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withObfuscated(true));
    }

    public MutableText STRIKETHROUGH(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableText REPLACE(MutableText text, String option){
        return Text.of(option).copy();
    }

    public MutableText MASK(MutableText text, String option){
        int length = text.getString().length();
        return Text.of(option.repeat(length)).copy();
    }

    public MutableText UPPER(MutableText text, String option){
        String string = text.getString();
        return Text.of(string.toUpperCase()).copy();
    }

    public MutableText LOWER(MutableText text, String option){
        String string = text.getString();
        return Text.of(string.toLowerCase()).copy();
    }
}
