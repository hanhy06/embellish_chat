package io.github.hanhy06.embellishchat.styling.data;

import io.github.hanhy06.embellishchat.styling.rule.StyleAction;

import java.util.List;

public record ParsedStyle(int begin, int end, String option, List<StyleAction> styles) {
    public static ParsedStyle of(int begin, int end, String option, List<StyleAction> styles){
        return new ParsedStyle(begin,end, option,styles);
    }
}
