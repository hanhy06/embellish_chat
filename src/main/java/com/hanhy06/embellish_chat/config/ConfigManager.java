package com.hanhy06.embellish_chat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.util.HexIntegerTypeAdapter;
import com.hanhy06.embellish_chat.util.PatternTypeAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigManager {
    public static ConfigManager INSTANCE;

    private static final String CONFIG_FILE_NAME = EmbellishChat.MOD_ID+".json";
    private final Path configFilePath;
    private Config config = Config.createDefault();

    private final List<ConfigListener> listeners = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(int.class,new HexIntegerTypeAdapter())
            .registerTypeAdapter(Integer.class,new HexIntegerTypeAdapter())
            .registerTypeAdapter(Pattern.class,new PatternTypeAdapter())
            .setPrettyPrinting()
            .setStrictness(Strictness.LENIENT)
            .disableHtmlEscaping()
            .create();

    public static Config getConfig(){
        return INSTANCE.config;
    }

    public ConfigManager(Path configDirPath){
        INSTANCE = this;

        this.configFilePath = configDirPath.resolve(CONFIG_FILE_NAME);

        if(!Files.exists(configFilePath)){
            try {
                Files.createFile(configFilePath);
                writeConfig();
            } catch (IOException e) {
                EmbellishChat.LOGGER.info("Failed to read config file. Using default config settings.");
            }
        }
    }

    public void readConfig(){
        Config loaded = null;

        try (BufferedReader reader = Files.newBufferedReader(configFilePath, StandardCharsets.UTF_8)) {
            loaded = gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to read config file: {}. Using default values.", configFilePath, e);
        } catch (JsonSyntaxException e) {
            EmbellishChat.LOGGER.error("Failed to parse config file: {}. Check JSON syntax. Using default values.", configFilePath, e);
        } catch (Exception e) {
            EmbellishChat.LOGGER.error("Unexpected error loading config file: {}. Using default values.", configFilePath, e);
        }

        if (loaded != null) {
            config = loaded;
            broadcastConfig();
            EmbellishChat.LOGGER.info("Config loaded successfully.");
        } else {
            writeConfig();
            broadcastConfig();
            EmbellishChat.LOGGER.warn("Config file is empty or invalid. Using default values.");
        }
    }

    public void writeConfig(){
        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(config, writer);
            EmbellishChat.LOGGER.info("Config saved successfully to {}", configFilePath);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to write config file: {}", configFilePath, e);
        } catch (Exception e) {
            EmbellishChat.LOGGER.error("Unexpected error saving config file: {}", configFilePath, e);
        }
    }

    public void addListener(ConfigListener listener){
        listeners.add(listener);
    }

    public void clearListener(){listeners.clear();}

    public void broadcastConfig(){
        for (ConfigListener listener : listeners){
            listener.onConfigReload(config);
        }
    }
}
