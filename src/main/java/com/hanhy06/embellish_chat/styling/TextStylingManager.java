package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.styling.utile.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.slice;

public class TextStylingManager implements ConfigListener {
    public LinkedHashMap<Pattern,TextStyleApplier> inChatStyling;
    public LinkedHashMap<Pattern,TextStyleApplier> inCommandStyling;
    public LinkedHashMap<Pattern,TextStyleApplier> inAnvilStyling;

    public TextStylingManager(Config config){
        onConfigReload(config);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = actionCaching(newConfig.inChatStyling());
        this.inCommandStyling = actionCaching(newConfig.inCommandStyling());
        this.inAnvilStyling = actionCaching(newConfig.inAnvilStyling());
    }

    public MutableText applyStyles(HashMap<Pattern,TextStyleApplier> actions,MutableText text){
        if (text.getString().isBlank()) return text;

        MutableText result = text;
        for (Map.Entry<Pattern, TextStyleApplier> action : actions.entrySet()){
            result = applyStyle(action.getKey(),action.getValue(),result);
        }

        return result;
    }

    private MutableText applyStyle(Pattern regex,TextStyleApplier applier,MutableText text){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        Matcher matcher = regex.matcher(runs.full());
        if (!matcher.find()) return text;

        int groupCount = matcher.groupCount();
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            String option = (groupCount == 1) ? "" : matcher.group(2);
            segment = applier.apply(segment, option);
            result.append(segment);

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private LinkedHashMap<Pattern,TextStyleApplier> actionCaching(LinkedHashMap<String,TextStyleApplier> actions){
        LinkedHashMap<Pattern,TextStyleApplier> action = new LinkedHashMap<>();
        for (Map.Entry<String,TextStyleApplier> entry : actions.entrySet()){
            try {
                action.put(Pattern.compile(entry.getKey()),entry.getValue());
            }catch (PatternSyntaxException e){
                EmbellishChat.LOGGER.error("regex error: ",e);
            }
        }
        return action;
    }
}
