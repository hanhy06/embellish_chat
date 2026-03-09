package io.github.hanhy06.embellishchat.config.configs;

import java.util.HashSet;
import java.util.UUID;

public record PlayerConfig(
        HashSet<UUID> BANNED_PLAYERS,
        HashSet<UUID> NOTIFY_OFF_PLAYERS
) {
    public static PlayerConfig createDefault(){
        return new PlayerConfig(
                new HashSet<>(),
                new HashSet<>()
        );
    }
}
