package com.hanhy06.betterchat.mention.data.playerdata;

import com.google.gson.Gson;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
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
    private final UserCache userCache;

    private static final String PLAYER_DATA_DIRECTORY_NAME = "playerdata";
    private final Path playerDataDirPath;

    private final Gson gson = new Gson();

    public PlayerDataManager(UserCache userCache, Path modDirPath) {
        this.userCache = userCache;

        playerDataDirPath = modDirPath.resolve(PLAYER_DATA_DIRECTORY_NAME);
        if(!Files.exists(playerDataDirPath)) {
            try {
                Files.createDirectories(playerDataDirPath);
            } catch (IOException e) {
                BetterChat.LOGGER.error("Failed to create player data directory :{}", playerDataDirPath);
            }
        }
    }

    public void savePlayerData(PlayerData playerData){
        Path playerDataSavePath = playerDataDirPath.resolve(playerData.getPlayerUUID().toString()+".json");

        try (BufferedWriter writer = Files.newBufferedWriter(playerDataSavePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(playerData, writer);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to write player data file: {}", playerData, e);
        }
    }

    public PlayerData loadPlayerData(UUID uuid){
        Path playerDataLoadPath = playerDataDirPath.resolve(uuid.toString()+".json");

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
