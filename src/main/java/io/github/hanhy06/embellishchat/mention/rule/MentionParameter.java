package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.mention.data.ParsedMention;
import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(ParsedMention parsedMention, ServerPlayerEntity sender, String option) {
    public static MentionParameter of(ParsedMention parsedMention,ServerPlayerEntity sender,String option){
        return new MentionParameter(parsedMention,sender,option);
    }
}
