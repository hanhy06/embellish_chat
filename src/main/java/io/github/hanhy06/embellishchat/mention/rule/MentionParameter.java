package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(String option,ServerPlayerEntity player) {
    public static MentionParameter of(String option,ServerPlayerEntity player){
        return new MentionParameter(PlaceHolderUtil.parsePlaceholder(option,player),player);
    }
}
