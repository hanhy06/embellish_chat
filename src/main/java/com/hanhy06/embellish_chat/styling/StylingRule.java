package com.hanhy06.embellish_chat.styling;

import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, StyleType styleType, String option) {
    public static StylingRule of(String regex, StyleType styleType, String option){
        return new StylingRule(Pattern.compile(regex),styleType,option);
    }
}
