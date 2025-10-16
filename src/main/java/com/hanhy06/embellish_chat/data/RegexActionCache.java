package com.hanhy06.embellish_chat.data;

import com.hanhy06.embellish_chat.styling.TextStyleApplier;

import java.util.regex.Pattern;

public record RegexActionCache(
        Pattern regex,
        TextStyleApplier applier
) {
    public static RegexActionCache of(RegexAction action){
        return new RegexActionCache(Pattern.compile(action.regex()), action.applier());
    }
}
