package com.hanhy06.embellish_chat.mention.rule;

import net.minecraft.server.network.ServerPlayerEntity;

public record MentionParameter(ServerPlayerEntity sender,String name) {
}
