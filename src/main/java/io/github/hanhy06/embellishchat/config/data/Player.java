package io.github.hanhy06.embellishchat.config.data;

import io.github.hanhy06.embellishchat.config.ConfigInterface;

import java.util.HashSet;
import java.util.UUID;

public record Player(
        HashSet<UUID> banned_players,
        HashSet<UUID> notify_off_players
) implements ConfigInterface {
    @Override
    public String getFileName() {
        return "players.json";
    }

    @Override
    public ConfigInterface getDefault() {
        return new Player(
                new HashSet<>(),
                new HashSet<>()
        );
    }
}
