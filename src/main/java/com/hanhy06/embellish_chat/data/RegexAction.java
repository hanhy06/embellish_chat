package com.hanhy06.embellish_chat.data;

import com.hanhy06.embellish_chat.text.TextStyleApplier;

public record RegexAction(
        String regex,
        TextStyleApplier applier
) {
    public static RegexAction of(String  regex,TextStyleApplier applier){
        return new RegexAction(regex, applier);
    }
}
