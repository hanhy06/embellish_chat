package io.github.hanhy06.embellishchat.styling.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordUtil {
    private final HttpClient client;

    public DiscordUtil(Config config) {
        this.client = HttpClient.newHttpClient();
    }

    public void send(URI uri,String content){
        if (uri.toString().isBlank()) {
            EmbellishChat.LOGGER.warn("The registered Discord DISCORD_WEBHOOK does not exist");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 204) {
                        EmbellishChat.LOGGER.error("Failed to send a message to Discord: {}", response.statusCode());
                    }
                })
                .exceptionally(exception -> {
                    EmbellishChat.LOGGER.error("Failed due to an error: {}", exception.getMessage());
                    return null;
                });
    }
}
