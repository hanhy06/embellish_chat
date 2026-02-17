package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TelemetryUtil {
    //TODO: 빌드할때 uri 바꿔야함
    private static final URI uri = URI.create("");
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> handle;

    public static void start(){
        if (handle != null) handle.cancel(false);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextRun;

        int currentHour = now.getHour();
        if (currentHour < 12) {
            nextRun = now.withHour(12).withMinute(0).withSecond(0);
        } else {
            nextRun = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        }

        long initialDelay = Duration.between(now, nextRun).toHours();
        handle = scheduler.scheduleAtFixedRate(() -> EmbellishChat.SERVER.execute(TelemetryUtil::send), initialDelay, 12, TimeUnit.HOURS);
    }

    private static void send(){
        String content = "{\"minecraftVersion\":\"%s\",\"playerCount\":%d,\"time\":\"%s\"}".formatted(
                EmbellishChat.SERVER.getVersion(),
                EmbellishChat.SERVER.getCurrentPlayerCount(),
                ZonedDateTime.now()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }
}
