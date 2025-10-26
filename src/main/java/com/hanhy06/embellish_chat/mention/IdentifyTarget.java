package com.hanhy06.embellish_chat.mention;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record IdentifyTarget(Integer targetColor, List<ServerPlayerEntity> players) {
}
