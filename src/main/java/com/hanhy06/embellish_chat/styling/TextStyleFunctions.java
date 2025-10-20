package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.styling.utile.Runs;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;

import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.slice;

public class TextStyleFunctions {
    public static MutableText PREPROCESSING_MENTION(MutableText text, Object option){
        return text;
    }

    public static MutableText PREPROCESSING_COLOR(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withColor(ConfigManager.getConfig().chatColor()));
    }

    public static MutableText PREPROCESSING_FONT(MutableText text, Object option){
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(ConfigManager.getConfig().chatFont()));
        return text.fillStyle(Style.EMPTY.withFont(font));
    }

    public static MutableText COLOR_HEX(MutableText text, Object option){
        int color = Color.decode(option.toString()).getRGB();
        return text.fillStyle(Style.EMPTY.withColor(color));
    }

    public static MutableText COLOR_PRESET(MutableText text, Object option){
        Config config = ConfigManager.getConfig();
        int color = config.colorPreset().getOrDefault(option.toString(),config.chatColor());
        return text.fillStyle(Style.EMPTY.withColor(color));
    }

    public static MutableText COLOR_RAINBOW(MutableText text, Object option){
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

    public static MutableText FONT(MutableText text, Object option){
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(option.toString()));
        return text.fillStyle(Style.EMPTY.withFont(font));
    }

    public static MutableText URL(MutableText text, Object option){
        try {
            URI uri = URI.create(option.toString());
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withColor(ConfigManager.getConfig().urlColor()));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", option);
            return text;
        }
    }

    public static MutableText BOLD(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withBold(true));
    }

    public static MutableText ITALIC(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withItalic(true));
    }

    public static MutableText UNDERLINE(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withUnderline(true));
    }

    public static MutableText STRIKETHROUGH(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public static MutableText OBFUSCATED(MutableText text, Object option){
        return text.fillStyle(Style.EMPTY.withObfuscated(true));
    }
}
