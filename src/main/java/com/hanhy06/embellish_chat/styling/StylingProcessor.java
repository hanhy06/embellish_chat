package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.flatten;
import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.slice;
import static java.util.Map.entry;

public class StylingProcessor implements ConfigListener {
    public List<StylingRule> inChatStyling;
    public List<StylingRule> inCommandStyling;
    public List<StylingRule> inAnvilStyling;

    private EnumMap<StyleType, BiFunction<MutableText,String,MutableText>> registers;

    public StylingProcessor(Config config){
        onConfigReload(config);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = newConfig.inChatStyling().stream().filter(Objects::nonNull).toList();
        this.inCommandStyling = newConfig.inCommandStyling().stream().filter(Objects::nonNull).toList();
        this.inAnvilStyling = newConfig.inAnvilStyling().stream().filter(Objects::nonNull).toList();

        StyleRegistry registry = new StyleRegistry(newConfig);
        this.registers = new EnumMap<>(Map.ofEntries(
                entry(StyleType.METADATA, registry::METADATA),
                entry(StyleType.COLOR_HEX, registry::COLOR_HEX),
                entry(StyleType.COLOR_RAINBOW, registry::COLOR_RAINBOW),
                entry(StyleType.COLOR_PRESET, registry::COLOR_PRESET),
                entry(StyleType.FONT, registry::FONT),
                entry(StyleType.URL, registry::URL),
                entry(StyleType.BOLD, registry::BOLD),
                entry(StyleType.ITALIC, registry::ITALIC),
                entry(StyleType.UNDERLINE, registry::UNDERLINE),
                entry(StyleType.STRIKETHROUGH, registry::STRIKETHROUGH),
                entry(StyleType.OBFUSCATED, registry::OBFUSCATED)
        ));
    }

    public MutableText applyStyles(List<StylingRule> styles, MutableText text){
        if (text.getString().isBlank() || styles.isEmpty()) return text;

        MutableText result = text;
        for (StylingRule style : styles){
            result = applyStyle(style.regex(),style.styleType(),result);
        }

        return result;
    }

    private MutableText applyStyle(Pattern regex, StyleType styleType, MutableText text){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        BiFunction<MutableText,String,MutableText> function = registers.get(styleType);
        Matcher matcher = regex.matcher(runs.full());
        if (!matcher.find()) return text;

        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            segment = function.apply(segment, matcher.group(2));
            result.append(segment);

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}
