package com.hanhy06.betterchat.playerdata;

import com.hanhy06.betterchat.mention.MentionData;
import net.minecraft.util.UserCache;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataCache;
    private final Map<UUID, List<MentionData>> mentionDataCache;
    private final ConcurrentLinkedQueue<Unit> mentionDataBuffer;

    private final PlayerDataIO playerDataIO;

    private final ScheduledExecutorService scheduler;

    public PlayerDataManager(UserCache cache, Path modDirPath) {
        this.playerDataCache = new ConcurrentHashMap<>();
        this.mentionDataCache = new ConcurrentHashMap<>();
        this.mentionDataBuffer = new ConcurrentLinkedQueue<>();

        this.playerDataIO = new PlayerDataIO(cache, modDirPath);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(this::bufferClearProcess, 0, 1, TimeUnit.MINUTES);
    }

    public void bufferClearProcess() {
        List<Unit> unitsToProcess = new ArrayList<>();
        Unit unit;
        while ((unit = mentionDataBuffer.poll()) != null) {
            unitsToProcess.add(unit);
        }

        if (unitsToProcess.isEmpty()) {
            return;
        }

        for (Unit currentUnit : unitsToProcess) {
            List<MentionData> mentionList = mentionDataCache.computeIfAbsent(
                    currentUnit.uuid,
                    k -> Collections.synchronizedList(new ArrayList<>())
            );

            mentionList.add(currentUnit.mention);

            PlayerData playerData = playerDataCache.get(currentUnit.uuid);

            if (playerData != null) {
                playerDataIO.saveMentionData(playerData, playerData.getLastPage(), mentionList);

                if (mentionList.size() >= 21) {
                    playerData.setLastPage(playerData.getLastPage() + 1);
                    playerDataIO.savePlayerData(playerData);
                    mentionDataCache.put(currentUnit.uuid, Collections.synchronizedList(new ArrayList<>()));
                }
            }
        }
    }

    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void bufferWrite(UUID uuid, MentionData data) {
        mentionDataBuffer.add(new Unit(uuid, data));
    }

    public Map<UUID, PlayerData> getPlayerDataCache() {
        return playerDataCache;
    }

    public Map<UUID, List<MentionData>> getMentionDataCache() {
        return mentionDataCache;
    }

    private record Unit(
            UUID uuid, MentionData mention
    ) {
    }
}