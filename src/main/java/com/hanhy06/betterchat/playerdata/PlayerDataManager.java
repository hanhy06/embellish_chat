package com.hanhy06.betterchat.playerdata;

import com.hanhy06.betterchat.mention.MentionData;
import com.hanhy06.betterchat.util.Teamcolor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.UserCache;

import java.nio.file.Path;
import java.util.ArrayList;
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
        if (mentionDataBuffer.isEmpty()) return;

        List<Unit> unitsToProcess = new ArrayList<>();
        Unit unit;
        while ((unit = mentionDataBuffer.poll()) != null) {
            unitsToProcess.add(unit);
        }

        for (Unit currentUnit : unitsToProcess) {
            List<MentionData> list =  mentionDataCache.get(currentUnit.uuid);
            list.add(currentUnit.mention);

            PlayerData playerData = playerDataCache.get(currentUnit.uuid);

            playerDataIO.saveMentionData(playerData,playerData.getLastPage(),list);

            if (list.size()==21){
                playerData.setLastPage(playerData.getLastPage()+1);
                playerDataIO.savePlayerData(playerData);
                mentionDataCache.put(currentUnit.uuid,new ArrayList<>());
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

    public void handlePlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        playerDataCache.put(uuid,playerDataIO.loadPlayerData(uuid));
    }

    public void handlePlayerLeave(ServerPlayNetworkHandler handler,MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData cacheData = playerDataCache.get(uuid);
        PlayerData playerData = new PlayerData(
                handler.getPlayer().getName().getString(),
                cacheData.getPlayerUUID(),
                cacheData.isNotificationsEnabled(),
                Teamcolor.getPlayerColor(handler.getPlayer()),
                cacheData.getLastPage()
        );
        playerDataIO.savePlayerData(playerData);

        playerDataCache.remove(uuid);
    }

    public PlayerData getPlayerData(UUID uuid){
        PlayerData result;
        if ((result = playerDataCache.get(uuid))!=null) return  result;
        else return playerDataIO.loadPlayerData(uuid);
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