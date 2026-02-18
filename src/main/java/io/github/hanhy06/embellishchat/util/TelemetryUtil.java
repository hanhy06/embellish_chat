package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;

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

public class TelemetryUtil implements ConfigListener {
    //TODO: 빌드할때 uri 바꿔야함
    private static final URI uri = URI.create("http://127.0.0.1:8000/api/metrics");
    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> handle;

    @Override
    public void onConfigReload(Config newConfig) {
        if (newConfig.enableTelemetry()) start();
        else if (handle!=null) handle.cancel(false);
    }

    public static void start(){
        if (handle != null) handle.cancel(false);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextRun;

        int currentHour = now.getHour();
        if (currentHour < 12) {
            nextRun = now.withHour(12).withMinute(0);
        } else {
            nextRun = now.plusDays(1).withHour(0).withMinute(0);
        }
        long initialDelay = Duration.between(now, nextRun).toHours();

        handle = scheduler.scheduleAtFixedRate(
                () -> EmbellishChat.SERVER.execute(TelemetryUtil::send),
                initialDelay,
                12,
                TimeUnit.HOURS
        );
    }

    private static void send(){
        String content = "{\"minecraftVersion\":\"%s\",\"playerCount\":%d}".formatted(
                EmbellishChat.SERVER.getVersion(),
                EmbellishChat.SERVER.getCurrentPlayerCount()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header(EmbellishChat.MOD_ID, "telemetry")
                .POST(HttpRequest.BodyPublishers.ofString(content))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }
}
