package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import com.hanhy06.embellish_chat.styling.rule.StyleRegistry;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;
import com.hanhy06.embellish_chat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.flatten;
import static com.hanhy06.embellish_chat.styling.util.TextSliceUtil.slice;

public class StylingProcessor implements ConfigListener {
    public static StylingProcessor INSTANCE;

    private Config config;
    private HashMap<String,List<StylingRule>> stylingRules;
    private StyleRegistry registry;

    public StylingProcessor(){
        INSTANCE = this;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;

        this.stylingRules = newConfig.stylingRules();
        this.registry = new StyleRegistry(newConfig);
    }

    public MutableText applyStylingRule(MutableText text, String key){
        if (text.getString().isBlank() || !stylingRules.containsKey(key)) return text;

        MutableText result = text;
        for (StylingRule style : stylingRules.get(key)){
            result = applyStyles(result,style);
        }

        return result;
    }

    private MutableText applyStyles(MutableText text,StylingRule style){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        Matcher matcher = style.pattern().matcher(runs.full());
        if (!matcher.find()) return text;

        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));

            String option = matcher.group(2);
            List<String> options = List.of();

            if (option != null && !option.isBlank()){
                options = List.of(option.split(config.delimiter()));
            }

            result.append(
                    applyStyle(
                            segment,
                            style.actions(),
                            options
                    )
            );

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private MutableText applyStyle(MutableText text, List<StyleAction> actions, List<String> options){
        MutableText result = text;

        int index = 0;
        for (StyleAction action : actions){
            BiFunction<MutableText,String,MutableText> function = registry.get(action.styleType());
            String option = action.preset();
            if (option.isBlank() && index < options.size()){
                option = options.get(index);
            }
            result = function.apply(result, option);
            index++;
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<Mention> mentions){
        return Text.empty();
    }
}