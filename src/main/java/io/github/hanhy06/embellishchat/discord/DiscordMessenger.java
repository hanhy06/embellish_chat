package io.github.hanhy06.embellishchat.discord;

import com.google.gson.Gson;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordMessenger implements ConfigListener {
    public static DiscordMessenger INSTANCE;

    private final HttpClient client;
    private final Gson gson;

    private DiscordProfile discordProfile;

    public DiscordMessenger() {
        INSTANCE = this;

        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.discordProfile = newConfig.discord();
    }

    public void sendJson(String json){
        send(json);
    }

    public void sendMessage(String content){
        DiscordPayload payload = DiscordPayload.of(discordProfile,content);
        send(gson.toJson(payload));
    }

    private void send(String content){
        if (discordProfile == null || discordProfile.webhook() == null) {
            EmbellishChat.LOGGER.warn("The registered Discord (or webhook) does not exist");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(discordProfile.webhook())
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
