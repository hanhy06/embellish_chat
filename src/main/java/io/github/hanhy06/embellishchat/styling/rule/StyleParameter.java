package io.github.hanhy06.embellishchat.styling.rule;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public record StyleParameter(MutableText text, String option, ServerPlayerEntity player) {
    public static StyleParameter of(MutableText text,String option,ServerPlayerEntity player){
        return new StyleParameter(text,option,player);
    }
}
