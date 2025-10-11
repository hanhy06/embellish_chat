package com.hanhy06.embellish_chat.data;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Objects;

public record Receiver(
        String name, int begin, int end, int teamColor, List<ServerPlayerEntity> players
){
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receiver other)) return false;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
