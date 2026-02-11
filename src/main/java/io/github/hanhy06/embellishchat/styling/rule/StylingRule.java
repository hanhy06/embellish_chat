package io.github.hanhy06.embellishchat.styling.rule;

import java.util.List;
import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, List<StyleAction> styles, String comment) {
    public static StylingRule of(String regex, List<StyleAction> styles, String comment){
        return new StylingRule(Pattern.compile(regex),styles,comment);
    }
}
