package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleParameter;
import io.github.hanhy06.embellishchat.styling.rule.StyleRegistry;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
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
            result = applyStyles(result,rule,player);
        }

        return result;
    }

    /**
     * @param text This is the full text to apply the style to.
     * @param pattern This is the compiled regular expression for finding the parts where styling should be applied.
     * @param functions These are the functions that apply styling.
     * @param presets These are the presets to be used in the functions. Their length must match that of the functions.
     * @param player This is the ServerPlayerEntity required for the Placeholder API and styling.
     * @return This is the text with styling applied.
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
            options = OptionUtil.parseOption(options,presets,player);
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

    private MutableText applyStyles(MutableText text,StylingRule style,ServerPlayerEntity player){
        Matcher matcher = style.pattern().matcher(text.getString());
        if (!matcher.find()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();
        int lastEnd = 0;

        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            List<String> options = OptionUtil.split(matcher.group(2),config.delimiter());
            result.append(applyStyle(segment, style.styles(), options,player));

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private MutableText applyStyle(MutableText text, List<StyleAction> actions, List<String> options,ServerPlayerEntity player){
        MutableText result = text;

        List<String> option = OptionUtil.parseOption(
                options,
                actions.stream().map(StyleAction::preset).toList(),
                player
        );

        for (int i=0;i<actions.size();i++){
            StyleAction action = actions.get(i);
            Function<StyleParameter, MutableText> function = registry.get(action.styleType());
            result = function.apply(StyleParameter.of(result, option.get(i),player));
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<Mention> mentions, ServerPlayerEntity player){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (Mention mention: mentions){
            result.append(slice(runs,lastEnd, mention.begin()));

            MutableText segment = slice(runs, mention.begin(), mention.end());
            segment.fillStyle(mention.style());
            for (StyleAction action : mention.rule().styles()) {
                String preset = PlaceHolderUtil
                        .getParedOption(action.preset(), player)
                        .getString();

                StyleParameter parameter = StyleParameter.of(segment, preset, player);

                segment = registry
                        .get(action.styleType())
                        .apply(parameter);
            }


            result.append(segment);
            lastEnd = mention.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}