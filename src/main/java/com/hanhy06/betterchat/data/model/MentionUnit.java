package com.hanhy06.betterchat.data.model;

import java.util.Objects;

public record MentionUnit(
        PlayerData receiver,
        int begin,
        int end) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MentionUnit unit = (MentionUnit) o;
        return Objects.equals(receiver, unit.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(receiver);
    }
}
