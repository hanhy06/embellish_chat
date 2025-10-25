package com.hanhy06.embellish_chat.styling;

import java.util.regex.Pattern;

//TODO: 하나의 페턴에 여러개의 타입과 옵션을 지정할수 있어야 하고 또 맨션 또안 스타일링 엔진에 합쳐야함
//TODO: 예를들어 볼드와 색상을 통해 MentionType의 here를 쓴다
public record StylingRule(Pattern pattern, StyleType styleType, String option) {
    public static StylingRule of(String regex, StyleType styleType, String option){
        return new StylingRule(Pattern.compile(regex),styleType,option);
    }
}
