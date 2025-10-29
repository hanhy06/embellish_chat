package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.mention.data.ParsedMention;
import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(ParsedMention parsedMention, ServerPlayerEntity sender, String mention) {
    public static MentionParameter of(ParsedMention parsedMention,ServerPlayerEntity sender,String mention){
        return new MentionParameter(parsedMention,sender,mention);
    }
}
