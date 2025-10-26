package com.hanhy06.embellish_chat.mention;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Objects;

public record MentionTarget(
        int begin, int end, MutableText text, List<ServerPlayerEntity> players
){

}
