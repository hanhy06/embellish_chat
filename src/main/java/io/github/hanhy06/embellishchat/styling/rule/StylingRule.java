package io.github.hanhy06.embellishchat.styling.rule;

import java.util.List;
import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, String comment, List<StyleAction> styles) {
    public static StylingRule of(String regex, String comment, List<StyleAction> styles){
        return new StylingRule(Pattern.compile(regex),comment,styles);
    }
}
