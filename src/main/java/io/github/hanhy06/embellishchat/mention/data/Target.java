package io.github.hanhy06.embellishchat.mention.data;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.HashSet;
import java.util.List;

public record Target(HashSet<ServerPlayerEntity> targets, Style style) {
    public static Target of(List<ServerPlayerEntity> targets,Style style){
        return new Target(new HashSet<>(targets),style);
    }
}
