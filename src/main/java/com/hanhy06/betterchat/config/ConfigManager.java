package com.hanhy06.betterchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hanhy06.betterchat.BetterChat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "config.json";

    private static volatile ConfigData configData = ConfigData.createDefault();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Path configFilePath = null;

    public static ConfigData getConfigData() {
        return configData;
    }

    public static void registerSaveAndLoad() {
        ServerLifecycleEvents.SERVER_STARTED.register(ConfigManager::handleServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ConfigManager::handleServerStop);
    }

    private static void handleServerStart(MinecraftServer server) {
        Path configDir = BetterChat.getModDirectoryPath();
        configFilePath = configDir.resolve(CONFIG_FILE_NAME);
        BetterChat.LOGGER.info("Loading BetterChat config from: {}", configFilePath);
        loadConfig();
    }

    private static void handleServerStop(MinecraftServer server) {
        if (configFilePath != null) {
            BetterChat.LOGGER.info("Saving BetterChat config to: {}", configFilePath);
            saveConfig();
        } else {
            BetterChat.LOGGER.error("Config file path not set, cannot save config.");
        }
    }

    private static synchronized void loadConfig() {
        if (configFilePath == null) {
            BetterChat.LOGGER.error("Cannot load config: configFilePath is null.");
            configData = ConfigData.createDefault();
            return;
        }

        if (!Files.exists(configFilePath)) {
            BetterChat.LOGGER.info("Config file not found. Creating default config file at: {}", configFilePath);
            configData = ConfigData.createDefault();
            saveConfig();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(configFilePath, StandardCharsets.UTF_8)) {
            ConfigData loaded = gson.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                configData = loaded;
                BetterChat.LOGGER.debug("Config loaded successfully.");
            } else {
                BetterChat.LOGGER.warn("Config file is empty or invalid. Using default values.");
                configData = ConfigData.createDefault();
            }
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to read config file: {}. Using default values.", configFilePath, e);
            configData = ConfigData.createDefault();
        } catch (JsonSyntaxException e) {
            BetterChat.LOGGER.error("Failed to parse config file: {}. Check JSON syntax. Using default values.", configFilePath, e);
            configData = ConfigData.createDefault();
        } catch (Exception e) {
            BetterChat.LOGGER.error("Unexpected error loading config file: {}. Using default values.", configFilePath, e);
            configData = ConfigData.createDefault();
        }
    }

    private static void saveConfig() {
        if (configFilePath == null) {
            BetterChat.LOGGER.error("Cannot save config: configFilePath is null.");
            return;
        }
        if (configData == null) {
            BetterChat.LOGGER.error("Cannot save config: configData is null.");
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(configData, writer);
            BetterChat.LOGGER.debug("Config saved successfully to {}", configFilePath);
        } catch (IOException e) {
            BetterChat.LOGGER.error("Failed to write config file: {}", configFilePath, e);
        } catch (Exception e) {
            BetterChat.LOGGER.error("Unexpected error saving config file: {}", configFilePath, e);
        }
    }
}