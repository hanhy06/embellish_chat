package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.data.ParsedStyle;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleParameter;
import io.github.hanhy06.embellishchat.styling.rule.StyleRegistry;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import io.github.hanhy06.embellishchat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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

    public List<ParsedStyle> parsedStyles(String text,String key){
        List<ParsedStyle> styles = new ArrayList<>();

        for (StylingRule rule : stylingRules.get(key)){
            styles.addAll(parsedStyle(rule,text));
        }

        return styles;
    }

    private List<ParsedStyle> parsedStyle(StylingRule rule,String text){
        Matcher matcher = rule.pattern().matcher(text);
        List<ParsedStyle> styles = new ArrayList<>();
        List<String> presets = rule.styles().stream().map(StyleAction::preset).toList();

        while (matcher.find()){
            int begin = matcher.start(1);
            int end = matcher.start(1);
            List<String> options = resolveOptions(matcher.group(2), presets);

            ParsedStyle style = ParsedStyle.of(begin,end,rule.styles(),options);
            styles.add(style);
        }

        return styles;
    }

    public MutableText applyStyle(MutableText text, ParsedStyle parsedStyle){
        Runs runs = flatten(text);
        MutableText result = slice(runs,parsedStyle.begin(),parsedStyle.end());

        for (StyleAction action : parsedStyle.styles()){
//            result = registry.get(action.styleType()).apply()
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<Mention> mentions){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (Mention mention : mentions){
            result.append(slice(runs,lastEnd,mention.begin()));

            MutableText segment = slice(runs, mention.begin(), mention.end());
            segment.fillStyle(mention.style());
            for (StyleAction action : mention.styles()){
                segment = registry
                        .get(action.styleType())
                        .apply(StyleParameter.of(segment,action.preset()));
            }

            result.append(segment);
            lastEnd = mention.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    public List<String> resolveOptions(String option,List<String> presets){
        List<String> options = List.of(option.split(config.delimiter()));
        List<String> result = new ArrayList<>();

        int index = 0;
        for (String preset : presets){
            String decided = preset;

            if (decided.isBlank() && index< options.size()){
                decided = options.get(index);
            }

            result.add(decided);
            index++;
        }

        return result;
    }
}