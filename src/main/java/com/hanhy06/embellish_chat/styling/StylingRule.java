package com.hanhy06.embellish_chat.styling;

import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, StyleType styleType) {
    public static StylingRule of(String regex, StyleType styleType){
        return new StylingRule(Pattern.compile(regex),styleType);
    }
}
