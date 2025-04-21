package com.hanhy06.betterchat.mention;

import com.google.gson.Gson;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.mention.data.PlayerData;
import com.mojang.authlib.GameProfile;
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
    private UserCache userCache;
    private Path modDirectoryPath;

    private final String PLAYER_DATA_DIRECTORY_NAME = "player_datas";
    private final Path playerDataDirectoryPath;

    private final Gson gson = new Gson();

    public PlayerDataManager(UserCache userCache, Path modDirectoryPath) {
        this.userCache = userCache;
        this.modDirectoryPath = modDirectoryPath;

        playerDataDirectoryPath = modDirectoryPath.resolve(PLAYER_DATA_DIRECTORY_NAME);
        if(!Files.exists(playerDataDirectoryPath)) {
            try {
                Files.createDirectories(playerDataDirectoryPath);
            } catch (IOException e) {
                BetterChat.LOGGER.error("Failed to create player data directory :{}",playerDataDirectoryPath);
            }
        }
    }

    public void savePlayerData(PlayerData playerData){
        Path playerDataSavePath = playerDataDirectoryPath.resolve(playerData.getPlayerUUID().toString()+".json");

        try (BufferedWriter writer = Files.newBufferedWriter(playerDataSavePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(playerData, writer);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to write player data file: {}", playerData, e);
        }
    }

    public PlayerData loadPlayerData(UUID uuid){
        Path playerDataLoadPath = playerDataDirectoryPath.resolve(uuid.toString()+".json");

        if(!Files.exists(playerDataLoadPath)){
            BetterChat.LOGGER.error("{} Player data file not found. create new file",uuid);

            String name = userCache.getByUuid(uuid)
                    .map(GameProfile::getName)
                    .orElseThrow(() -> new IllegalStateException("Cannot find player name for UUID: " + uuid));
            PlayerData playerData = new PlayerData(name,uuid,true);

            savePlayerData(playerData);
            return playerData;
        }

        try(BufferedReader reader = Files.newBufferedReader(playerDataLoadPath,StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, PlayerData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPlayerData(UUID uuid, MentionData mentionData){
        PlayerData data = loadPlayerData(uuid);
        data.addMentionData(mentionData);
        savePlayerData(data);
    }
}
