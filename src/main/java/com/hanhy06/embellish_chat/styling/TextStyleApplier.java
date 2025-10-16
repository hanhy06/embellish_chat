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

public enum TextStyleApplier {
    COLOR_HEX{
        public MutableText apply(MutableText text,String option){
            int color = Color.decode(option).getRGB();
            return text.fillStyle(Style.EMPTY.withColor(color));
        }
    },
    COLOR_PRESET{
        public MutableText apply(MutableText text,String option){
            Config config = ConfigManager.getConfig();
            int color = config.colorPreset().getOrDefault(option,config.chatColor());
            return text.fillStyle(Style.EMPTY.withColor(color));
        }
    },
    COLOR_RAINBOW{
        public MutableText apply(MutableText text,String option){
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
    },
    COLOR_GRADIENT{
        public MutableText apply(MutableText text,String option){
            return Text.empty();
        }
    },
    COLOR_SHADOW{
        public MutableText apply(MutableText text,String option){
            return Text.empty();
        }
    },
    FONT{
        public MutableText apply(MutableText text,String option){
            StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(option));
            return text.fillStyle(Style.EMPTY.withFont(font));
        }
    },
    URL{
        public MutableText apply(MutableText text,String option){
            try {
                URI uri = URI.create(option);
                ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
                return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withColor(ConfigManager.getConfig().urlColor()));
            } catch (IllegalArgumentException e) {
                EmbellishChat.LOGGER.warn("Invalid URL address: {}", option);
                return text;
            }
        }
    },
    BOLD{
        public MutableText apply(MutableText text,String option){
            return text.fillStyle(Style.EMPTY.withBold(true));
        }
    },
    ITALIC{
        public MutableText apply(MutableText text,String option){
            return text.fillStyle(Style.EMPTY.withItalic(true));
        }
    },
    UNDERLINE{
        public MutableText apply(MutableText text,String option){
            return text.fillStyle(Style.EMPTY.withUnderline(true));
        }
    },
    STRIKETHROUGH{
        public MutableText apply(MutableText text,String option){
            return text.fillStyle(Style.EMPTY.withStrikethrough(true));
        }
    },
    OBFUSCATED{
        public MutableText apply(MutableText text,String option){
            return text.fillStyle(Style.EMPTY.withObfuscated(true));
        }
    }
}
