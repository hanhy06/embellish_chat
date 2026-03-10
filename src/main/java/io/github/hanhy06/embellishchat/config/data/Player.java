package io.github.hanhy06.embellishchat.config.data;

import java.util.HashSet;
import java.util.UUID;

public record Player(
        HashSet<UUID> banned_players,
        HashSet<UUID> notify_off_players
) {
    public static final String PLAYER_FILE_NAME = "players.json";
    public static final Player DEFAULT = new Player(
            new HashSet<>(),
            new HashSet<>()
    );
}
