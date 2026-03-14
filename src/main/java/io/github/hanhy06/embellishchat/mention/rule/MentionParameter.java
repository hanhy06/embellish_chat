package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.level.ServerPlayer;

public record MentionParameter(String option,ServerPlayer player) {
    public static MentionParameter of(String option,ServerPlayer player){
        return new MentionParameter(PlaceHolderUtil.parsePlaceholder(option,player),player);
    }
}
