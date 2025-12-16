package io.github.hanhy06.embellishchat.discord;

import com.google.gson.Gson;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordMessenger implements ConfigListener {
    public static DiscordMessenger INSTANCE;
    private final HttpClient client;
    private final Gson gson = new Gson();

    private Discord discord;

    public DiscordMessenger() {
        INSTANCE = this;

        this.client = HttpClient.newHttpClient();
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.discord = newConfig.discord();
    }

    public void sendJson(String json){
        send(json);
    }

    public void sendMessage(String content){
        DiscordPayload payload = DiscordPayload.of(discord,content);
        String jsonPayload = gson.toJson(payload);
        send(jsonPayload);
    }

    private void send(String content){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(discord.webhook())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 204) {
                EmbellishChat.LOGGER.error("Failed to send a message to Discord: {}",response.statusCode());
            }
        }catch (IOException | InterruptedException exception){
            EmbellishChat.LOGGER.error("Failed due to an I/O or interruption error (IOException | InterruptedException): {}",exception.getMessage());
        }
    }
}
