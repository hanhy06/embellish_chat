package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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

    private EnumMap<StyleType, BiFunction<MutableText,String,MutableText>> appliers;

    public StylingProcessor(Config config){
        onConfigReload(config);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = newConfig.inChatStyling().stream().filter(Objects::nonNull).toList();
        this.inCommandStyling = newConfig.inCommandStyling().stream().filter(Objects::nonNull).toList();
        this.inAnvilStyling = newConfig.inAnvilStyling().stream().filter(Objects::nonNull).toList();

        StyleRegistry functions = new StyleRegistry(newConfig);
        this.appliers = new EnumMap<>(Map.ofEntries(
                entry(StyleType.COLOR_HEX, functions::COLOR_HEX),
                entry(StyleType.COLOR_RAINBOW, functions::COLOR_RAINBOW),
                entry(StyleType.COLOR_PRESET, functions::COLOR_PRESET),
                entry(StyleType.FONT, functions::FONT),
                entry(StyleType.URL, functions::URL),
                entry(StyleType.BOLD, functions::BOLD),
                entry(StyleType.ITALIC, functions::ITALIC),
                entry(StyleType.UNDERLINE, functions::UNDERLINE),
                entry(StyleType.STRIKETHROUGH, functions::STRIKETHROUGH),
                entry(StyleType.OBFUSCATED, functions::OBFUSCATED)
        ));
    }

    public MutableText applyStyles(List<StylingRule> styles, MutableText text){
        if (text.getString().isBlank() || styles.isEmpty()) return text;

        MutableText result = text;
        for (StylingRule style : styles){
            result = applyStyle(style.regex(),style.applier(),result);
        }

        return result;
    }

    private MutableText applyStyle(Pattern regex, StyleType applier, MutableText text){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        BiFunction<MutableText,String,MutableText> function = appliers.get(applier);
        Matcher matcher = regex.matcher(runs.full());
        if (!matcher.find()) return text;

        int groupCount = matcher.groupCount();
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            String option = (groupCount == 1) ? "" : matcher.group(2);
            segment = function.apply(segment, option);
            result.append(segment);

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}
