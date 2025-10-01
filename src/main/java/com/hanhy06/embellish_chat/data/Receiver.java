package com.hanhy06.embellish_chat.data;

import java.util.Objects;

public record Receiver(
        String name, int begin, int end, int teamColor, boolean isOnline
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
