package com.hanhy06.betterchat.mention;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.PlayerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.UserCache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class PlayerDataManager {
    private static final String PLAYER_DATAS_DIRECTOR_NAME = "player-datas";
    private static Path playerDataDirectoryPath;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void setPlayerDataDirectoryPath(){
        playerDataDirectoryPath = BetterChat.getModDirectoryPath().resolve(PLAYER_DATAS_DIRECTOR_NAME);

        if (!Files.exists(playerDataDirectoryPath)){
            try {
                Files.createDirectory(playerDataDirectoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static PlayerConnectState isPlayerConnection(String name){
        MinecraftServer server = BetterChat.getServerInstance();
        PlayerManager manager = server.getPlayerManager();

        UserCache userCache = server.getUserCache();

        if (manager.getPlayer(name)!=null){
            return PlayerConnectState.ONLINE;
        } else if (!userCache.findByName(name).isPresent()) {
            return PlayerConnectState.OFFLINE;
        }else {
            return PlayerConnectState.NEVER_CONNECTED;
        }
    }

    public static void savePlayerData(PlayerData data){
        Path path = playerDataDirectoryPath.resolve(data.getPlayerUUID().toString());
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(data,writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlayerData loadPlayerData(UUID uuid){
        Path path = playerDataDirectoryPath.resolve(uuid.toString());

        if (!Files.exists(path)){
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            PlayerData loaded = gson.fromJson(reader, PlayerData.class);
            return loaded;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
