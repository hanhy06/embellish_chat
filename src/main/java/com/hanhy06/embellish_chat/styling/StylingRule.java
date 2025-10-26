package com.hanhy06.embellish_chat.styling;

import java.util.List;
import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, List<StyleAction> actions) {
    public static StylingRule of(String regex, List<StyleAction> actions){
        return new StylingRule(Pattern.compile(regex),actions);
    }
}
