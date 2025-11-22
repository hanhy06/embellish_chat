package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.mention.data.Mention;
import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(Mention mention, ServerPlayerEntity sender, String option) {
    public static MentionParameter of(Mention mention,ServerPlayerEntity sender,String option){
        return new MentionParameter(mention,sender,option);
    }
}
