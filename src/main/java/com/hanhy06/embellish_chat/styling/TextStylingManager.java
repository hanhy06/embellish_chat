package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
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
        Matcher matcher = action.regex().matcher(runs.full());
        if (!matcher.find()) return text;

        int groupCount = matcher.groupCount();
        int lastEnd = 0;
        MutableText result = Text.empty();
        do {
            result.append(slice(runs, lastEnd, matcher.start()));

            if (groupCount == 1){
                result.append(
                        action.applier().apply(
                                slice(runs, matcher.start(1), matcher.end(1)),
                                ""
                        )
                );
            }else {
                result.append(
                        action.applier().apply(
                                slice(runs, matcher.start(1), matcher.end(1)),
                                matcher.group(2)
                        )
                );
            }

            lastEnd = matcher.end();
        } while (matcher.find());
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    private List<RegexActionCache> actionCaching(List<RegexAction> actions){
        return actions.stream().map(RegexActionCache::of).filter(Objects::nonNull).toList();
    }
}
