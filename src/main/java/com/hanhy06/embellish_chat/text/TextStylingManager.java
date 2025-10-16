package com.hanhy06.embellish_chat.text;

import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.text.utile.TextStyleUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hanhy06.embellish_chat.text.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.text.utile.TextStyleUtils.slice;

public class TextStylingManager {
    private static final String BLOCK_RAW = "(?<!\\\\)\\[(.+?)](%s)+?";
    private final Pattern BLOCK;
    private final Pattern OPTION;

    private final HashMap<String , BiFunction<MutableText,String,MutableText>> actions = new HashMap<>(
            Map.ofEntries(
                    Map.entry("<(#[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})>", TextStyleUtils::applyHexColor),
                    Map.entry("<([^#]+?)>", TextStyleUtils::applyPresetColor),
                    Map.entry("<(rainbow)>", TextStyleUtils::applyRainbowColor),
                    Map.entry("\\((https://[^\\s)]+?)\\)", TextStyleUtils::applyURI),
                    Map.entry("\\{(.+?)}", TextStyleUtils::applyFont)
            )
    );

    private final Config config;

    public TextStylingManager(Config config){
        this.config = config;

        String options = String.join(
                "|",
                actions.keySet()
        );

        BLOCK = Pattern.compile(String.format(BLOCK_RAW,options));
        OPTION = Pattern.compile(options);
    }

    public MutableText applyStyle(MutableText text){
        TextStyleUtils.Runs runs = flatten(text);
        MutableText result = Text.empty();

        List<Target> targets = identifyTarget(text);

        int lastEnd = 0;
        for (Target target : targets){
            result.append(slice(runs,lastEnd,target.start));

            MutableText content = target.text;
            List<String> options = identifyOptions(target.options);
            for (String option : options){
                content = matchOption(option).apply(content,option);
            }

            result.append(content);
            lastEnd = target.end;
        }
        result.append(slice(runs,lastEnd,runs.full().length()));

        return result;
    }

    record Target(MutableText text, String options, int start, int end){}

    private List<Target> identifyTarget(MutableText text){
        Matcher matcher = BLOCK.matcher(text.getString());
        TextStyleUtils.Runs runs = flatten(text);

        List<Target> targets = new ArrayList<>();
        while (matcher.find()){
            int start = matcher.start(1);
            int end = matcher.end(1);

            targets.add(
                    new Target(
                            slice(runs,start, end),
                            matcher.group(2),
                            start,
                            end
                    )
            );
        }

        return targets;
    }

    private List<String> identifyOptions(String string){
        Matcher matcher = OPTION.matcher(string);

        List<String> options = new ArrayList<>();
        while (matcher.find()){
            options.add(matcher.group(1));
        }

        return options;
    }

    private BiFunction<MutableText,String,MutableText> matchOption(String option){
        for (String regex : actions.keySet()){
            if (option.matches(regex)) return actions.get(regex);
        }
        return null;
    }

}
