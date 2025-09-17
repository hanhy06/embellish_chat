package com.hanhy06.embellish_chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hanhy06.embellish_chat.EmbellishChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigManager {
    public static ConfigManager INSTANCE;

    private final String configFileName = EmbellishChat.MOD_ID+".json";
    private final Path configFilePath;
    public ConfigData config = ConfigData.createDefault();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager(Path configDirPath){
        INSTANCE = this;

        this.configFilePath = configDirPath.resolve(configFileName);

        if(Files.exists(configFilePath)){
            try {
                Files.createFile(configFilePath);
                writeConfig();
            } catch (IOException e) {
                EmbellishChat.LOGGER.info("Failed to read config file. Using default config settings.");
            }
        }
    }

    public void readConfig(){
        try (BufferedReader reader = Files.newBufferedReader(configFilePath, StandardCharsets.UTF_8)) {
            ConfigData loaded = gson.fromJson(reader, ConfigData.class);
            if (loaded != null) {
                config = loaded;
                EmbellishChat.LOGGER.debug("Config loaded successfully.");
            } else {
                config = ConfigData.createDefault();
                EmbellishChat.LOGGER.warn("Config file is empty or invalid. Using default values.");
            }
        } catch (IOException e) {
            config = ConfigData.createDefault();
            EmbellishChat.LOGGER.error("Failed to read config file: {}. Using default values.", configFilePath, e);
        } catch (JsonSyntaxException e) {
            config = ConfigData.createDefault();
            EmbellishChat.LOGGER.error("Failed to parse config file: {}. Check JSON syntax. Using default values.", configFilePath, e);
        } catch (Exception e) {
            config = ConfigData.createDefault();
            EmbellishChat.LOGGER.error("Unexpected error loading config file: {}. Using default values.", configFilePath, e);
        }
    }

    public void writeConfig(){
        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(config, writer);
            EmbellishChat.LOGGER.debug("Config saved successfully to {}", configFilePath);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to write config file: {}", configFilePath, e);
        } catch (Exception e) {
            EmbellishChat.LOGGER.error("Unexpected error saving config file: {}", configFilePath, e);
        }
    }
}
