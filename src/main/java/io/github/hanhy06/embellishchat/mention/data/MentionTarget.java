package io.github.hanhy06.embellishchat.mention.data;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import java.util.Objects;

public record MentionTarget(ServerPlayerEntity player, SoundEvent sound, Text title) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MentionTarget that = (MentionTarget) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(player);
    }
}
