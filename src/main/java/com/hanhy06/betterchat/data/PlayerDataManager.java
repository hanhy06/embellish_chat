package com.hanhy06.betterchat.data;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.storage.DatabaseManager;
import com.hanhy06.betterchat.util.Teamcolor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class PlayerDataManager {
//    TODO: 나중에 구글 구아바 캐싱 기능 사용 미싱이 일어 날때 10분 로드
    private final Map<UUID, PlayerData> playerDataCache;
    private final Map<UUID, List<MentionData>> mentionDataCache;
    private final ConcurrentLinkedQueue<Unit> mentionDataBuffer;

    private final DatabaseManager databaseManager;

    private final ScheduledExecutorService scheduler;

    private static final int INVENTORY_SIZE_7x3 = 21;

    public PlayerDataManager(DatabaseManager databaseManager) {
        this.playerDataCache = new ConcurrentHashMap<>();
        this.mentionDataCache = new ConcurrentHashMap<>();
        this.mentionDataBuffer = new ConcurrentLinkedQueue<>();

        this.databaseManager = databaseManager;

        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void handleServerStart() {
        scheduler.scheduleAtFixedRate(this::bufferClearProcess, 0, 1, TimeUnit.MINUTES);
    }

    public void bufferClearProcess() {
        if (mentionDataBuffer.isEmpty()) return;

        BetterChat.LOGGER.info("Executing buffer clear process");

        List<Unit> unitsToProcess = new ArrayList<>();
        Unit unit;
        while ((unit = mentionDataBuffer.poll()) != null) {
            unitsToProcess.add(unit);
        }

        for (Unit currentUnit : unitsToProcess) {
            databaseManager.writeMentionData(currentUnit.mention);
        }
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

    public void bufferWrite(UUID uuid, MentionData data) {
        mentionDataBuffer.add(new Unit(uuid, data));
    }

    public void handlePlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();
        int mention_count = databaseManager.countMentionData(uuid);

        playerDataCache.put(uuid,databaseManager.readPlayerData(uuid));
        mentionDataCache.put(uuid,databaseManager.readMentionData(uuid,mention_count-INVENTORY_SIZE_7x3,mention_count));
    }

    public void handlePlayerLeave(ServerPlayNetworkHandler handler,MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData cacheData = playerDataCache.get(uuid);
        PlayerData playerData = new PlayerData(
                handler.getPlayer().getName().getString(),
                cacheData.getPlayerUUID(),
                cacheData.isNotificationsEnabled(),
                Teamcolor.getPlayerColor(handler.getPlayer())
        );
        databaseManager.savePlayerData(playerData);

        playerDataCache.remove(uuid);
        mentionDataCache.remove(uuid);
    }

    public PlayerData getPlayerData(UUID uuid){
        PlayerData result;
        if ((result = playerDataCache.get(uuid))!=null) return result;
        else return databaseManager.readPlayerData(uuid);
    }

    public PlayerData getPlayerData(String name){
        return databaseManager.readPlayerData(name);
    }

    public List<MentionData> getMentionData(UUID uuid){
        List<MentionData> result;
        if ((result = mentionDataCache.get(uuid))!=null) return result;
        else{
            int mention_count = databaseManager.countMentionData(uuid);
            return databaseManager.readMentionData(uuid,mention_count-INVENTORY_SIZE_7x3,mention_count);
        }
    }

    public List<MentionData> getMentionData(UUID uuid,int pageNumber){
        int mention_page = (databaseManager.countMentionData(uuid)/INVENTORY_SIZE_7x3)*pageNumber;
        return databaseManager.readMentionData(uuid,mention_page-INVENTORY_SIZE_7x3,mention_page);
    }

    private record Unit(
            UUID uuid, MentionData mention
    ) {
    }
}