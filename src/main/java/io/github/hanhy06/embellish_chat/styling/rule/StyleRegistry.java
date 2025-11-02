package io.github.hanhy06.embellish_chat.styling.rule;

import io.github.hanhy06.embellish_chat.EmbellishChat;
import io.github.hanhy06.embellish_chat.config.Config;
import io.github.hanhy06.embellish_chat.styling.util.Runs;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.github.hanhy06.embellish_chat.styling.util.TextSliceUtil.flatten;
import static io.github.hanhy06.embellish_chat.styling.util.TextSliceUtil.slice;
import static java.util.Map.entry;

public class StyleRegistry {
    private final Config config;
    private final DateTimeFormatter timestamp;
    private final HashMap<String, Integer> colorPreset;
    private final EnumMap<StyleType, Function<StyleParameter, MutableText>> registers;

    public StyleRegistry(Config config) {
        this.config = config;
        this.timestamp = DateTimeFormatter.ofPattern(config.timestamp());
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

    public Function<StyleParameter, MutableText> get(StyleType styleType) {
        return registers.get(styleType);
    }

    public MutableText METADATA(StyleParameter parameter) {
        MutableText text = parameter.text();
        String now = LocalDateTime.now().format(timestamp);

        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(now + "\nClick to copy to clipboard"));
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(now + " " + text.getString());

        return text.fillStyle(Style.EMPTY
                .withHoverEvent(hoverEvent)
                .withClickEvent(clickEvent));
    }

    public MutableText COLOR_HEX(StyleParameter parameter) {
        int color = Color.decode(parameter.option()).getRGB();
        return parameter.text().fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_RAINBOW(StyleParameter parameter) {
        Runs runs = flatten(parameter.text());
        String string = runs.full();
        int length = string.length();
        float saturation = Float.parseFloat(parameter.option());

        MutableText result = Text.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, saturation, 1f);
            result.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return result;
    }

    public MutableText COLOR_PRESET(StyleParameter parameter) {
        int color = colorPreset.getOrDefault(parameter.option(), 0xFFFFFF);
        return parameter.text().fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_SHADOW(StyleParameter parameter) {
        int color = Color.decode(parameter.option()).getRGB();
        return parameter.text().fillStyle(Style.EMPTY.withShadowColor(color));
    }

    public MutableText FONT(StyleParameter parameter) {
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(parameter.option()));
        return parameter.text().fillStyle(Style.EMPTY.withFont(font));
    }

    public MutableText URL(StyleParameter parameter) {
        try {
            URI uri = URI.create(parameter.option());
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return parameter.text().fillStyle(Style.EMPTY
                    .withClickEvent(clickEvent)
                    .withColor(config.urlColor()));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", parameter.option());
            return parameter.text();
        }
    }

    public MutableText BOLD(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withBold(true));
    }

    public MutableText ITALIC(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withItalic(true));
    }

    public MutableText UNDERLINE(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withUnderline(true));
    }

    public MutableText OBFUSCATED(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withObfuscated(true));
    }

    public MutableText STRIKETHROUGH(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableText REPLACE(StyleParameter parameter) {
        return Text.of(parameter.option()).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText MASK(StyleParameter parameter) {
        int length = parameter.text().getString().length();
        return Text.of(parameter.option().repeat(length)).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText UPPER(StyleParameter parameter) {
        String string = parameter.text().getString();
        return Text.of(string.toUpperCase()).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText LOWER(StyleParameter parameter) {
        String string = parameter.text().getString();
        return Text.of(string.toLowerCase()).copy().fillStyle(parameter.text().getStyle());
    }
}