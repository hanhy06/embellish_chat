package io.github.hanhy06.embellishchat.styling.data;

import io.github.hanhy06.embellishchat.styling.rule.StyleAction;

import java.util.List;

public record ParsedStyle(int begin, int end, List<StyleAction> actions) {
    public static ParsedStyle of(int begin, int end, List<StyleAction> actions){
        return new ParsedStyle(begin,end,actions);
    }
}
