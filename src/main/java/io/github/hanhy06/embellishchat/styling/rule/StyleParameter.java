package io.github.hanhy06.embellishchat.styling.rule;

import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public record StyleParameter(MutableText segment, Text option, ServerPlayerEntity player) {
    public static StyleParameter of(MutableText segment,String option,ServerPlayerEntity player){
        return new StyleParameter(segment, PlaceHolderUtil.parsedText(option,player),player);
    }

    public String getStringOption(){
        return this.option.getString();
    }

    public Text getTextOption(){
        return this.option;
    }
}
