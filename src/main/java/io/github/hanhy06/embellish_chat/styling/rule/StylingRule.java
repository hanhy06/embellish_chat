package io.github.hanhy06.embellish_chat.styling.rule;

import java.util.List;
import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, List<StyleAction> styles) {
    public static StylingRule of(String regex, List<StyleAction> styles){
        return new StylingRule(Pattern.compile(regex),styles);
    }
}
