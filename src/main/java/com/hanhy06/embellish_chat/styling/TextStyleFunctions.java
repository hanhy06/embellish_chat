package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.styling.utile.Runs;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.HashMap;

import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.slice;

public class TextStyleFunctions {
    private final Config config;
    private final HashMap<String,Integer> colorPreset;
    private final int chatColor;
    private final StyleSpriteSource chatFont;

    public TextStyleFunctions(Config config){
        this.config = config;

        String font = config.chatFont();
        this.colorPreset = config.colorPreset();
        this.chatColor = config.chatColor();
        this.chatFont = font.isBlank() ? null : new  StyleSpriteSource.Font(Identifier.tryParse(font));
    }

    public MutableText PREPROCESSING_MENTION(MutableText text, String option){
        return text;
    }

    public MutableText PREPROCESSING_COLOR(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withColor(chatColor));
    }

    public MutableText PREPROCESSING_FONT(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withFont(chatFont));
    }

    public MutableText COLOR_HEX(MutableText text, String option){
        int color = Color.decode(option).getRGB();
        return text.fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_RAINBOW(MutableText text, String option){
        Runs runs = flatten(text);
        String string = runs.full();
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

    public MutableText COLOR_PRESET(MutableText text, String option){
        Config config = ConfigManager.getConfig();
        int color = colorPreset.getOrDefault(option,chatColor);
        return text.fillStyle(Style.EMPTY.withColor(color));
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

    public MutableText STRIKETHROUGH(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableText OBFUSCATED(MutableText text, String option){
        return text.fillStyle(Style.EMPTY.withObfuscated(true));
    }
}
