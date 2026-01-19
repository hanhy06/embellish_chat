package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(ServerPlayerEntity sender, String option) {
    public static MentionParameter of(ServerPlayerEntity sender,String option){
        return new MentionParameter(sender, PlaceHolderUtil.parsedText(option,sender).getString());
    }
}
