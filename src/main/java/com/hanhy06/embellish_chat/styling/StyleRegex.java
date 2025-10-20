package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.EmbellishChat;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record StyleRegex(Pattern regex, TextStyleApplier applier) {
    public static StyleRegex of(Pattern regex, TextStyleApplier applier){
        return new StyleRegex(regex,applier);
    }

    public static StyleRegex of(String regex, TextStyleApplier applier){
        try {
            return new StyleRegex(Pattern.compile(regex),applier);
        }catch (PatternSyntaxException e){
            EmbellishChat.LOGGER.error("regex error:",e);
            return null;
        }
    }
}
