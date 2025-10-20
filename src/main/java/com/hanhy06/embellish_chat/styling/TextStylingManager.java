package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.RegexAction;
import com.hanhy06.embellish_chat.data.RegexActionCache;
import com.hanhy06.embellish_chat.styling.utile.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.slice;

public class TextStylingManager implements ConfigListener {
    public List<RegexActionCache> inChatStyling;
    public List<RegexActionCache> inCommandStyling;
    public List<RegexActionCache> inAnvilStyling;

    public TextStylingManager(Config config){
        onConfigReload(config);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = actionCaching(newConfig.inChatStyling());
        this.inCommandStyling = actionCaching(newConfig.inCommandStyling());
        this.inAnvilStyling = actionCaching(newConfig.inAnvilStyling());
    }

    public MutableText applyStyles(List<RegexActionCache> actions,MutableText text){
        if (text.getString().isBlank()) return text;

        MutableText result = text;
        for (RegexActionCache action : actions){
            result = applyStyle(action,result);
        }

        return result;
    }

    private MutableText applyStyle(RegexActionCache action,MutableText text){
        Runs runs = flatten(text);
        MutableText result = Text.empty();

        Matcher matcher = action.regex().matcher(runs.full());
        if (!matcher.find()) return text;

        int groupCount = matcher.groupCount();
        int lastEnd = 0;
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            MutableText segment = slice(runs, matcher.start(1), matcher.end(1));
            String option = (groupCount == 1) ? "" : matcher.group(2);
            segment = action.applier().apply(segment, option);
            result.append(segment);

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private List<RegexActionCache> actionCaching(List<RegexAction> actions){
        return actions.stream().map(RegexActionCache::of).filter(Objects::nonNull).toList();
    }
}
