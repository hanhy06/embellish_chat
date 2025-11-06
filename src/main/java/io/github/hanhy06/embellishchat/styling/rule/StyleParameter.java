package io.github.hanhy06.embellishchat.styling.rule;

import net.minecraft.text.MutableText;

public record StyleParameter(MutableText text,String option) {
    public static StyleParameter of(MutableText text,String option){
        return new StyleParameter(text,option);
    }
}
