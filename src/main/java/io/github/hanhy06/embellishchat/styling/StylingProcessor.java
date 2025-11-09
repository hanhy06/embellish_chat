package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.data.ParsedStyle;
import io.github.hanhy06.embellishchat.styling.data.StyleSegment;
import io.github.hanhy06.embellishchat.styling.rule.*;
import io.github.hanhy06.embellishchat.styling.util.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Function;
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

    public MutableText applyStyle(MutableText text,List<StyleSegment> segments){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (StyleSegment styleSegment : segments){
            result.append(slice(runs,lastEnd,styleSegment.begin()));

            MutableText segment = slice(runs,styleSegment.begin(),styleSegment.end());
            Map<StyleType,String> operation = styleSegment.operation();
            for (StyleType styleType : operation.keySet()){
                StyleParameter parameter = StyleParameter.of(segment,operation.get(styleType));
                segment = registry.get(styleType).apply(parameter);
            }

            result.append(segment);
            lastEnd = styleSegment.end();
        }
        result.append(slice(runs,lastEnd,runs.full().length()));

        return result;
    }

    public Map<StylingRule,List<ParsedStyle>> parsedStyles(String text,String key){
        Map<StylingRule,List<ParsedStyle>> parsedStyles = new LinkedHashMap<>();

        for (StylingRule rule : stylingRules.get(key)){
            parsedStyles.put(rule,parsedStyle(text,rule));
        }

        return parsedStyles;
    }

    private List<ParsedStyle> parsedStyle(String text,StylingRule rule){
        Matcher matcher = rule.pattern().matcher(text);
        List<ParsedStyle> parsedStyles = new ArrayList<>();
        List<StyleAction> styles = rule.styles();

        while (matcher.find()){
            int begin = matcher.start(1);
            int end = matcher.end(1);
            String option = matcher.group(2);

            ParsedStyle style = ParsedStyle.of(begin,end,option,styles);
            parsedStyles.add(style);
        }

        return parsedStyles;
    }

    public StyleSegment parsedSegment(ParsedStyle parsedStyle){
        List<StyleType> types = parsedStyle.styles().stream().map(StyleAction::styleType).toList();
        List<String> presets = parsedStyle.styles().stream().map(StyleAction::preset).toList();
        List<String> options = resolveOptions(parsedStyle.option(),presets);

        Map<StyleType,String> operation = new LinkedHashMap<>();
        for (int i=0;i< types.size();i++){
            operation.put(types.get(i),options.get(i));
        }

        return StyleSegment.of(parsedStyle.begin(),parsedStyle.end(),operation);
    }

    private List<String> resolveOptions(String option,List<String> presets){
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
}