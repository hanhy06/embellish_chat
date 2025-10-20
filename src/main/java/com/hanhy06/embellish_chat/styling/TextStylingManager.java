package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.styling.utile.Runs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.flatten;
import static com.hanhy06.embellish_chat.styling.utile.TextStyleUtils.slice;

public class TextStylingManager implements ConfigListener {
    public List<StyleRegex> inChatStyling;
    public List<StyleRegex> inCommandStyling;
    public List<StyleRegex> inAnvilStyling;

    public TextStylingManager(Config config){
        onConfigReload(config);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = newConfig.inChatStyling().stream().filter(Objects::nonNull).toList();
        this.inCommandStyling = newConfig.inCommandStyling().stream().filter(Objects::nonNull).toList();
        this.inAnvilStyling = newConfig.inAnvilStyling().stream().filter(Objects::nonNull).toList();
    }

    public MutableText applyStyles(List<StyleRegex> styles,MutableText text){
        if (text.getString().isBlank() || styles.isEmpty()) return text;

        MutableText result = text;
        for (StyleRegex style : styles){
            result = applyStyle(style.regex(),style.applier(),result);
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
}
