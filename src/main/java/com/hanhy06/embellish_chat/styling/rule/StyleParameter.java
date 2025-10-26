package com.hanhy06.embellish_chat.styling.rule;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

public record StyleParameter(ServerPlayerEntity sender, MutableText text,String option) {
}
