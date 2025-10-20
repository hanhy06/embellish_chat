package com.hanhy06.embellish_chat.styling;

import java.util.regex.Pattern;

public record StyleRegex(Pattern regex, TextStyleApplier applier) {
    public static StyleRegex of(String regex, TextStyleApplier applier){
        return new StyleRegex(Pattern.compile(regex),applier);
    }
}
