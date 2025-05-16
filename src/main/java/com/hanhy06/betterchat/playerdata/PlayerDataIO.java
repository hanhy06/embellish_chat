package com.hanhy06.betterchat.playerdata;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.MentionData;
import com.mojang.authlib.GameProfile;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataIO {
    private final UserCache userCache;

    private static final String PLAYER_DATA_DIRECTORY_NAME = "playerdata";
    private final Path playerDataDirPath;

    private final Gson gson = new Gson();

    public PlayerDataIO(UserCache userCache, Path modDirPath) {
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

    public void saveMentionData(PlayerData playerData, int pageNumber, List<MentionData> mentionData){
        Path mentionDataSavePath = playerDataDirPath.resolve(playerData.getPlayerUUID().toString()).resolve("%s.json".formatted(pageNumber));

        if (!Files.exists(mentionDataSavePath)){
            try {
                Files.createDirectories(mentionDataSavePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(mentionDataSavePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(mentionData, writer);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to write mention data file: {}", mentionDataSavePath, e);
        }
    }

    public List<MentionData> loadMentionData(UUID uuid, int pageNumber){
        Path mentionDataLoadPath = playerDataDirPath.resolve(uuid.toString()).resolve("%s.json".formatted(pageNumber));

        if(!Files.exists(mentionDataLoadPath)){
            BetterChat.LOGGER.error("{} mention data file not found.",mentionDataLoadPath);
            return new ArrayList<>();
        }

        try(BufferedReader reader = Files.newBufferedReader(mentionDataLoadPath,StandardCharsets.UTF_8)) {
            Type mentionListType = new TypeToken<ArrayList<MentionData>>() {}.getType();
            return gson.fromJson(reader, mentionListType);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to read mention data file: {}",mentionDataLoadPath);
        }
        return null;
    }

    public void savePlayerData(PlayerData playerData){
        Path playerDataSavePath = playerDataDirPath.resolve(playerData.getPlayerUUID().toString()).resolve(playerData.getPlayerUUID().toString()+".json");

        if (Files.exists(playerDataSavePath)){
            try {
                Files.createDirectories(playerDataSavePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(playerDataSavePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(playerData, writer);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to write player data file: {}", playerDataSavePath, e);
        }
    }

    public PlayerData loadPlayerData(UUID uuid){
        Path playerDataLoadPath = playerDataDirPath.resolve(uuid.toString()).resolve(uuid.toString()+".json");

        if(!Files.exists(playerDataLoadPath)){
            BetterChat.LOGGER.error("{} Player data file not found. create new file",playerDataLoadPath);

            String name = userCache.getByUuid(uuid)
                    .map(GameProfile::getName)
                    .orElseThrow(() -> new IllegalStateException("Cannot find player name for UUID: " + uuid));
            PlayerData playerData = new PlayerData(name,uuid,true, TextColor.fromFormatting(Formatting.YELLOW),0);

            savePlayerData(playerData);
            return playerData;
        }

        try(BufferedReader reader = Files.newBufferedReader(playerDataLoadPath,StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, PlayerData.class);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to read player data file: {}",playerDataLoadPath);
        }
        return null;
    }
}
