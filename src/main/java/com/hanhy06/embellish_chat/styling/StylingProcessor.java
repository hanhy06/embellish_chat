package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.MentionTarget;
import com.hanhy06.embellish_chat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.flatten;
import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.slice;
import static java.util.Map.entry;

public class StylingProcessor implements ConfigListener {
    public static StylingProcessor INSTANCE;

    private HashMap<String,List<StylingRule>> stylingRules;
    private EnumMap<StyleType, BiFunction<MutableText,String,MutableText>> registers;

    public StylingProcessor(){
        INSTANCE = this;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.stylingRules = newConfig.stylingRules();

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

    public MutableText applyStyles(MutableText text,String key){
        if (text.getString().isBlank()) return text;

        MutableText result = text;
        for (StylingRule style : stylingRules.get(key)){
            result = applyStyle(style,result);
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<MentionTarget> targets){
        return StyleRegistry.MENTION(text,targets);
    }

    private MutableText applyStyle(StylingRule style, MutableText text){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        BiFunction<MutableText,String,MutableText> function = registers.get(style.styleType());
        Matcher matcher = style.pattern().matcher(runs.full());
        String option = style.option();
        if (!matcher.find()) return text;

        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            if (option.isBlank()) option = matcher.group(2);

            segment = function.apply(segment, option);
            result.append(segment);

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}
