package com.hanhy06.betterchat.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.storage.DatabaseManager;
import com.hanhy06.betterchat.util.Teamcolor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class PlayerDataManager {
    private final DatabaseManager databaseManager;

    private final LoadingCache<UUID, PlayerData> playerDataCache;
    private final ConcurrentLinkedQueue<MentionData> mentionDataBuffer;

    private final ScheduledExecutorService scheduler;

    private static final int INVENTORY_SIZE_7x3 = 21;

    public PlayerDataManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;

        playerDataCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, PlayerData>() {
                    @Override
                    public @NotNull PlayerData load(@NotNull UUID key) throws Exception {
                        return databaseManager.readPlayerData(key);
                    }
                });

        this.mentionDataBuffer = new ConcurrentLinkedQueue<>();

        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void handleServerStart() {
        scheduler.scheduleAtFixedRate(this::bufferClearProcess, 0, 1, TimeUnit.MINUTES);
    }

    public void bufferClearProcess() {
        if (mentionDataBuffer.isEmpty()) return;

        BetterChat.LOGGER.info("Executing buffer clear process");

        List<MentionData> mentionDatas = new ArrayList<>();
        MentionData mentionData;
        while ((mentionData = mentionDataBuffer.poll()) != null) {
            mentionDatas.add(mentionData);
        }

        for (MentionData data : mentionDatas) {
            databaseManager.writeMentionData(data);
        }

        BetterChat.LOGGER.info("Successfully cleared buffer and recorded {} mention data entries to the database.", mentionDatas.size());
    }

    public void handleServerStop() {
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

    public void bufferWrite(MentionData data) {
        mentionDataBuffer.add(data);
    }

    public void handlePlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData playerData;
        if ((playerData = databaseManager.readPlayerData(uuid)) == null){
            playerData = new PlayerData(
                    handler.getPlayer().getName().getString(),
                    uuid,
                    true,
                    Teamcolor.getPlayerColor(handler.getPlayer())
            );
            databaseManager.savePlayerData(playerData);
        }
    }

    public void handlePlayerLeave(ServerPlayNetworkHandler handler,MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData cacheData;
        try {
            cacheData = playerDataCache.get(uuid);

            if(cacheData == null) return;

            PlayerData playerData = new PlayerData(
                    handler.getPlayer().getName().getString(),
                    cacheData.getPlayerUUID(),
                    cacheData.isNotificationsEnabled(),
                    Teamcolor.getPlayerColor(handler.getPlayer())
            );
            databaseManager.savePlayerData(playerData);
        } catch (ExecutionException e) {
            BetterChat.LOGGER.error("Failed to update player data for player uuid: {}",uuid);
        }finally {
            playerDataCache.invalidate(uuid);
        }
    }

    public PlayerData getPlayerData(UUID uuid){
        PlayerData result = null;
        try {
            if ((result = playerDataCache.get(uuid))!=null) return result;
        } catch (ExecutionException e) {
            BetterChat.LOGGER.error("Failed to get player data for player uuid: {}",uuid);
        }
        return result;
    }

    public void addPlayerData(PlayerData playerData){
        databaseManager.savePlayerData(playerData);
    }

    public PlayerData getPlayerData(String name){
        return databaseManager.readPlayerData(name);
    }

    public List<MentionData> getMentionData(UUID uuid,int pageNumber){
        int mention_page = (databaseManager.countMentionData(uuid)/INVENTORY_SIZE_7x3)*pageNumber;
        return databaseManager.readMentionData(uuid,mention_page-INVENTORY_SIZE_7x3,mention_page);
    }
}