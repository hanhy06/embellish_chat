package io.github.hanhy06.embellishchat.mention.data;

import java.util.HashSet;
import java.util.List;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public record Target(HashSet<ServerPlayer> targets, Style style) {
    public static Target of(List<ServerPlayer> targets,Style style){
        return new Target(new HashSet<>(targets),style);
    }

    public static Target of(HashSet<ServerPlayer> targets,Style style){
        return new Target(targets,style);
    }
}
