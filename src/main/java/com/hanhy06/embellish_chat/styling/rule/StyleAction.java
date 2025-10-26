package com.hanhy06.embellish_chat.styling.rule;

public record StyleAction(StyleType styleType,String preset) {
    public static StyleAction of(StyleType styleType,String preset){
        return new StyleAction(styleType,preset);
    }
}
