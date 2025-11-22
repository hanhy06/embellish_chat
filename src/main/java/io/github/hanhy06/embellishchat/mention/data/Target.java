package io.github.hanhy06.embellishchat.mention.data;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.List;

public record Target(List<ServerPlayerEntity> targets, Style style) {
}
