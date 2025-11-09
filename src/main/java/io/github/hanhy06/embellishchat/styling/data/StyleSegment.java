package io.github.hanhy06.embellishchat.styling.data;

import io.github.hanhy06.embellishchat.styling.rule.StyleType;

import java.util.Map;

public record StyleSegment(int begin, int end, Map<StyleType,String> operation) {
    public static StyleSegment of(int begin, int end, Map<StyleType,String> operation){
        return new StyleSegment(begin,end,operation);
    }
}
