package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import net.minecraft.server.network.ServerPlayerEntity;

import java.time.Instant;
import java.util.Objects;

public record Cooldown(ServerPlayerEntity player, Instant end, MentionRule rule) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cooldown cooldown = (Cooldown) o;
        return Objects.equals(rule, cooldown.rule) && Objects.equals(player, cooldown.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, rule);
    }
}
