package io.github.hanhy06.embellishchat.discord;

import com.google.gson.Gson;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordMessenger {
    private final HttpClient client;
    private URI webhook;

    public DiscordMessenger(Config config) {
        this.client = HttpClient.newHttpClient();
        this.webhook = config.discord();
    }

    public void send(String content){
        if (webhook == null) {
            EmbellishChat.LOGGER.warn("The registered Discord webhook does not exist");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(webhook)
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
