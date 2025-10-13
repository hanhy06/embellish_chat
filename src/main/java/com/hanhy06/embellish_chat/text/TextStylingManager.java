package com.hanhy06.embellish_chat.text;

import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.RegexAction;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hanhy06.embellish_chat.text.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.text.TextStyleUtils.slice;

public class TextStylingManager {
    private static final String BLOCK = "(?<!\\\\)\\[(.+?)](%s)+?";

    private final List<RegexAction> actions = List.of(
            RegexAction.of("<(#[A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})>", TextStyleUtils::applyHexColor),
            RegexAction.of("<([^#]+?)>", TextStyleUtils::applyPresetColor),
            RegexAction.of("<(rainbow)>", TextStyleUtils::applyRainbowColor),
            RegexAction.of("\\((https://[^\\s)]+?)\\)", TextStyleUtils::applyURI),
            RegexAction.of("\\{(.+?)}", TextStyleUtils::applyFont)
    );

    private final Pattern block;
    private final Pattern option;

    private final Config config;

    public TextStylingManager(Config config){
        this.config = config;

        String string = actions.stream().map(RegexAction::regex).collect(Collectors.joining("|"));

        block = Pattern.compile(String.format(BLOCK,string));
        option = Pattern.compile(string);
    }

    public MutableText applyStyle(MutableText text){
        TextStyleUtils.Runs runs = flatten(text);
        MutableText result = Text.empty();
        String raw = text.getString();

        Matcher blockMatcher = block.matcher(raw);
        int lastEnd = 0;

        while (blockMatcher.find()){
            result.append(slice(runs, lastEnd, blockMatcher.start()));

            MutableText content = slice(runs, blockMatcher.start(1), blockMatcher.end(1));

            String optionStr = blockMatcher.group(2);
            List<String> options = new ArrayList<>();

            Matcher optionMatcher = option.matcher(optionStr);
            while (optionMatcher.find()){
                options.add(optionMatcher.group(1));
            }

            for (String option : options){
                for (RegexAction regexAction : actions){
                    if (option.matches(regexAction.regex())){
                        content = regexAction.function().apply(content,option);
                    }
                }
            }

            result.append(content);
            lastEnd = blockMatcher.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

}
