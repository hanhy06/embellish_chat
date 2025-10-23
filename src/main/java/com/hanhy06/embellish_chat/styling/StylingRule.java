package com.hanhy06.embellish_chat.styling;

import java.util.regex.Pattern;

public record StylingRule(Pattern regex, StyleType applier) {
    public static StylingRule of(String regex, StyleType applier){
        return new StylingRule(Pattern.compile(regex),applier);
    }
}
