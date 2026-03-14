package io.github.hanhy06.embellishchat.styling.rule;

import java.util.List;
import java.util.regex.Pattern;

public record StyleRule(Pattern pattern, String comment, List<StyleAction> styles) {
    public static StyleRule of(String regex, String comment, List<StyleAction> styles){
        return new StyleRule(Pattern.compile(regex), comment,styles);
    }
}
