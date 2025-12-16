package io.github.hanhy06.embellishchat.discord;

import java.net.URI;

public record Discord(
        URI webhook,
        String name,
        String avatar_url
) {
}
