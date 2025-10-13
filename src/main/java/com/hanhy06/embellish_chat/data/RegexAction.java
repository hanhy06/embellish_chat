package com.hanhy06.embellish_chat.data;

import net.minecraft.text.MutableText;

import java.util.function.BiFunction;

public record RegexAction(
        String regex,
        BiFunction<MutableText,String,MutableText> function
) {
    public static RegexAction of(String  regex,BiFunction<MutableText,String,MutableText> function){
        return new RegexAction(regex, function);
    }
}
