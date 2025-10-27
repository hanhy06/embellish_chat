package com.hanhy06.embellish_chat.mention.data;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.List;

public record ParsedTarget(List<ServerPlayerEntity> players, Style style) {
    public static ParsedTarget of(List<ServerPlayerEntity> players, Style style){
        return new ParsedTarget(players,style);
    }
}
