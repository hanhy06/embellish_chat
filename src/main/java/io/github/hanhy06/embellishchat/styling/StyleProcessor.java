package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.data.Runs;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleParameter;
import io.github.hanhy06.embellishchat.styling.rule.StyleRegistry;
import io.github.hanhy06.embellishchat.styling.rule.StyleRule;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.flatten;
import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.slice;

public class StyleProcessor implements ConfigListener {
    public static StyleProcessor INSTANCE;

    private Config config;
    private Map<String,List<StyleRule>> stylingRules;
    private StyleRegistry registry;

    public StyleProcessor(){
        INSTANCE = this;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;

        this.stylingRules = newConfig.style_rules();
        this.registry = new StyleRegistry(newConfig);
    }

    public MutableComponent handleStyle(MutableComponent text,List<String> keys,ServerPlayer player){
        List<StyleRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(stylingRules.getOrDefault(key,List.of())));
        if (rules.isEmpty()) return text;

        MutableComponent result = text;
        for (StyleRule rule : rules) {
            result = applyStyleRule(result,rule,player);
        }

        return result;
    }

    private MutableComponent applyStyleRule(MutableComponent text, StyleRule style, ServerPlayer player){
        Matcher matcher = style.pattern().matcher(text.getString());
        if (!matcher.find()) return text;

        MutableComponent result = Component.empty();
        Runs runs = flatten(text);

        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableComponent segment = slice(runs, matcher.start(1), matcher.end(1));
            result.append(applyStyle(segment, style.styles(), matcher.group(2), player));

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    public MutableComponent applyMention(MutableComponent text, List<Mention> mentions, ServerPlayer player){
        if (mentions.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableComponent result = Component.empty();

        int lastEnd = 0;
        for (Mention mention: mentions){
            if (mention.begin() < lastEnd) continue;
            result.append(slice(runs,lastEnd, mention.begin()));

            MutableComponent segment = slice(runs, mention.begin(), mention.end());
            if (mention.style() != null) segment.withStyle(mention.style());
            result.append(applyStyle(segment,mention.rule().styles(),"",player));

            lastEnd = mention.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private MutableComponent applyStyle(MutableComponent segment,List<StyleAction> actions,String option,ServerPlayer player){
        MutableComponent result = segment;
        List<String> options = OptionUtil.split(option,config.delimiter());

        for (int i=0;i<actions.size();i++){
            StyleAction action = actions.get(i);

            Function<StyleParameter, MutableComponent> function = registry.get(action.styleType());
            String selectOption = OptionUtil.selectOption(
                    action.preset(),
                    options.size() > i ? options.get(i):""
            );

            result = function.apply(StyleParameter.of(result,selectOption,player));
        }

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
    public MutableComponent applyStyleAPI(MutableComponent text, Pattern pattern,List<Function<StyleParameter,MutableComponent>> functions,List<String> presets,ServerPlayer player){
        Matcher matcher = pattern.matcher(text.getString());
        if (!matcher.find()) return text;

        Runs runs = flatten(text);
        MutableComponent result = Component.empty();

        int lastEnd = 0;
        do {
            result.append(slice(runs,lastEnd,matcher.start()));

            MutableComponent segment = slice(runs, matcher.start(1), matcher.end(1));
            List<String> options = OptionUtil.split(matcher.group(2),config.delimiter());
            for (int i=0;i<functions.size();i++){
                String option = OptionUtil.selectOption(presets.get(i),options.size() > i ? options.get(i):"");
                StyleParameter parameter = StyleParameter.of(segment,option,player);
                segment = functions.get(i).apply(parameter);
            }

            result.append(segment);
            lastEnd = matcher.end();
        }while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}