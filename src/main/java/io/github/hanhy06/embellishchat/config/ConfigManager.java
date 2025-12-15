package io.github.hanhy06.embellishchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.ColorTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.PatternTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.SoundEventTypeAdapter;
import net.minecraft.sound.SoundEvent;

import java.awt.*;
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
            .registerTypeAdapter(Pattern.class,new PatternTypeAdapter())
            .registerTypeAdapter(SoundEvent.class,new SoundEventTypeAdapter())
            .registerTypeAdapter(Color.class,new ColorTypeAdapter())
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
                EmbellishChat.LOGGER.warn("Failed to create config file. Using default settings.", e);
            }
        }
    }

    public boolean readConfig(){
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

        if (loaded != null && loaded.version() != null && loaded.version().equals(config.version())) {
            config = loaded;
            broadcastConfig();
            EmbellishChat.LOGGER.info("Config loaded successfully.");
            return true;
        } else if (loaded != null) {
            broadcastConfig();
            EmbellishChat.LOGGER.warn("Config version mismatch or missing. Using default config. Please review and update your config file.");
            return false;
        } else {
            broadcastConfig();
            EmbellishChat.LOGGER.warn("Config file is empty or invalid. Using default config.");
            return false;
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

    public void broadcastConfig(){
        for (ConfigListener listener : listeners){
            listener.onConfigReload(config);
        }
    }
}
