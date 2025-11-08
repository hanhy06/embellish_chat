package io.github.hanhy06.embellishchat.styling.data;

import java.util.List;

public record ParsedStyle(int begin, int end, List<String> options) {
    public static ParsedStyle of(int begin, int end, List<String> options){
        return new ParsedStyle(begin,end, options);
    }
}
