package io.github.hanhy06.embellishchat.styling.rule;

import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public record StyleParameter(MutableComponent segment, Component option, ServerPlayer player) {
    public static StyleParameter of(MutableComponent segment,String option,ServerPlayer player){
        return new StyleParameter(segment, PlaceHolderUtil.parseText(option,player),player);
    }

    public String getString(){
        return this.option.getString();
    }

    public Component getText(){
        return option;
    }
}
