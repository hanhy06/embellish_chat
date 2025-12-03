package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.mention.rule.MentionRule;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Cooldown(UUID uuid, Instant end, MentionRule rule) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cooldown cooldown = (Cooldown) o;
        return Objects.equals(rule, cooldown.rule) && Objects.equals(uuid, cooldown.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, rule);
    }
}
