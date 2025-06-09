package com.hanhy06.betterchat.data.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.config.ConfigLoadedListener;
import com.hanhy06.betterchat.data.model.PlayerData;
import com.hanhy06.betterchat.data.repository.PlayerDataRepository;
import com.hanhy06.betterchat.util.Teamcolor;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayerDataService implements ConfigLoadedListener {
    private final PlayerDataRepository playerDataRepository;

    private LoadingCache<UUID, PlayerData> playerDataCache = null;

    public PlayerDataService(PlayerDataRepository playerDataRepository) {
        this.playerDataRepository = playerDataRepository;
    }

    public void handlePlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData playerData = playerDataRepository.readPlayerData(uuid);

        if (playerData == null){
            playerData = new PlayerData(
                    handler.getPlayer().getName().getString(),
                    uuid,
                    true,
                    Teamcolor.getPlayerColor(handler.getPlayer())
            );
            playerDataRepository.savePlayerData(playerData);
        }
    }

    public void handlePlayerLeave(ServerPlayNetworkHandler handler,MinecraftServer server){
        UUID uuid = handler.getPlayer().getUuid();

        PlayerData cacheData;
        try {
            cacheData = playerDataCache.get(uuid);

            PlayerData playerData = new PlayerData(
                    handler.getPlayer().getName().getString(),
                    cacheData.getPlayerUUID(),
                    cacheData.isNotificationsEnabled(),
                    Teamcolor.getPlayerColor(handler.getPlayer())
            );
            playerDataRepository.savePlayerData(playerData);
        } catch (ExecutionException e) {
            BetterChat.LOGGER.error("Failed to update player data for player uuid: {}",uuid);
        }finally {
            playerDataCache.invalidate(uuid);
        }
    }

    public PlayerData getPlayerData(UUID uuid){
        try {
            return playerDataCache.get(uuid);
        } catch (ExecutionException e) {
            BetterChat.LOGGER.error("Failed get player data uuid: {}",uuid);
        }
        return null;
    }

    public void savePlayerData(PlayerData playerData){
        playerDataRepository.savePlayerData(playerData);
    }

    @Override
    public void onConfigLoaded(ConfigData newConfigData) {
        playerDataCache = CacheBuilder.newBuilder()
                .maximumSize(newConfigData.maxPlayerDataCacheSize())
                .expireAfterWrite(newConfigData.playerDataCacheTTLMinutes(), TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, PlayerData>() {
                    @Override
                    public @NotNull PlayerData load(@NotNull UUID key) {
                        PlayerData playerData = playerDataRepository.readPlayerData(key);
                        return (playerData!=null) ? playerData : PlayerData.createDefaultPlayerData(key);
                    }
                });
    }
}
