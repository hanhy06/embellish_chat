package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.rule.*;
import io.github.hanhy06.embellishchat.styling.util.Runs;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.flatten;
import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.slice;

public class StylingProcessor implements ConfigListener {
    public static StylingProcessor INSTANCE;

    private Config config;
    private Map<String,List<StylingRule>> stylingRules;
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

    public MutableText handleStyle(MutableText text,List<String> keys,ServerPlayerEntity player){
        List<StylingRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(stylingRules.get(key)));
        MutableText result = text;

        for (StylingRule rule : rules) {
            result = applyStyleRule(result,rule,player);
        }

        return result;
    }

    private MutableText applyStyleRule(MutableText text, StylingRule style, ServerPlayerEntity player){
        Matcher matcher = style.pattern().matcher(text.getString());
        if (!matcher.find()) return text;

        MutableText result = Text.empty();

        List<StyleType> types = new ArrayList<>();
        List<String> presets = new ArrayList<>();
        style.styles().forEach(action ->{
            types.add(action.styleType());
            presets.add(action.preset());
        });

        Runs runs = flatten(text);
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            List<String> options = OptionUtil.split(matcher.group(2),config.delimiter());
            options = OptionUtil.selectOption(options,presets);
            result.append(applyStyle(segment, types, options, player));

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private MutableText applyStyle(MutableText segment, List<StyleType> types, List<String> options,ServerPlayerEntity player){
        MutableText result = segment;

        for (int i=0;i<types.size();i++){
            Function<StyleParameter, MutableText> function = registry.get(types.get(i));
            result = function.apply(StyleParameter.of(result,options.get(i),player));
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<Mention> mentions, ServerPlayerEntity player){
        if (mentions.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (Mention mention: mentions){
            if (mention.begin() < lastEnd) continue;
            result.append(slice(runs,lastEnd, mention.begin()));

            MutableText segment = slice(runs, mention.begin(), mention.end());
            if (mention.style() != null) segment.fillStyle(mention.style());
            for (StyleAction style : mention.rule().styles()) {
                StyleParameter parameter = StyleParameter.of(segment, style.preset(), player);
                segment = registry.get(style.styleType()).apply(parameter);
            }

            result.append(segment);
            lastEnd = mention.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    /**
     * @param text This is the full segment to apply the style to.
     * @param pattern This is the compiled regular expression for finding the parts where styling should be applied.
     * @param functions These are the functions that apply styling.
     * @param presets These are the presets to be used in the functions. Their length must match that of the functions.
     * @param player This is the ServerPlayerEntity required for the Placeholder API and styling.
     * @return This is the segment with styling applied.
     */
    public MutableText applyStyleAPI(MutableText text, Pattern pattern,List<Function<StyleParameter,MutableText>> functions,List<String> presets,ServerPlayerEntity player){
        Matcher matcher = pattern.matcher(text.getString());
        if (!matcher.find()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();
        int lastEnd = 0;

        do {
            result.append(slice(runs,lastEnd,matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            List<String> options = OptionUtil.split(matcher.group(2),config.delimiter());
            options = OptionUtil.selectOption(options,presets);
            for (int i=0;i<functions.size();i++){
                StyleParameter parameter = StyleParameter.of(segment,options.get(i),player);
                segment = functions.get(i).apply(parameter);
            }

            result.append(segment);
            lastEnd = matcher.end();
        }while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}