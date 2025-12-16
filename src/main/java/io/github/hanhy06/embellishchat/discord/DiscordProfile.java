package io.github.hanhy06.embellishchat.discord;

import java.net.URI;

public record DiscordProfile(
        URI webhook,
        String name,
        String avatar
) {
}
