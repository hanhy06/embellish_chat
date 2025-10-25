package com.hanhy06.embellish_chat.styling;

import java.util.List;
import java.util.regex.Pattern;

//TODO: 맨션 또안 스타일링 엔진에 합쳐야함
//TODO: 예를들어 볼드와 색상을 통해 MentionType의 here를 쓴다
public record StylingRule(Pattern pattern, List<StyleAction> actions) {
    public static StylingRule of(String regex, List<StyleAction> actions){
        return new StylingRule(Pattern.compile(regex),actions);
    }
}
