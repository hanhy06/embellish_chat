package io.github.hanhy06.embellishchat.config;

import com.google.gson.*;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.*;
import io.github.hanhy06.embellishchat.config.data.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

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
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class ConfigManager {
    public static ConfigManager INSTANCE;
    public final Object LOCK_KEY = new Object();

    private static final String CONFIG_FILE_DIR = EmbellishChat.MOD_ID;

    private final Path configDirPath;

    public static Config CONFIG = Config.DEFAULT;
    public static Mention MENTION = Mention.DEFAULT;
    public static Style STYLE = Style.DEFAULT;
    public static Preset PRESET = Preset.DEFAULT;
    public static Player PLAYER = Player.DEFAULT;

    private final List<ConfigListener> listeners = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pattern.class, new PatternTypeAdapter())
            .registerTypeAdapter(SoundEvent.class, new SoundEventTypeAdapter())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .registerTypeAdapter(MutableText.class, new MutableTextAdapter())
            .setPrettyPrinting()
            .setStrictness(Strictness.LENIENT)
            .disableHtmlEscaping()
            .create();


    public ConfigManager(Path configBasePath) {
        INSTANCE = this;
        this.configDirPath = configBasePath.resolve(CONFIG_FILE_DIR);

        try {
            if (!Files.exists(configDirPath)) {
                Files.createDirectories(configDirPath);
                writeConfig(this::writeJsonFile);
            }else {
                writeConfig(this::writeIfAbsent);
            }
        } catch (IOException e) {
            EmbellishChat.LOGGER.warn("Failed to create config files. Using default settings.", e);
        }
    }

    public boolean readConfig() {
        JsonObject configJson = readJsonFile(Config.CONFIG_FILE_NAME);
        JsonObject styleJson = readJsonFile(Style.STYLE_FILE_NAME);
        JsonObject mentionJson = readJsonFile(Mention.MENTION_FILE_NAME);
        JsonObject presetJson = readJsonFile(Preset.PRESET_FILE_NAME);
        JsonObject playerJson = readJsonFile(Player.PLAYER_FILE_NAME);

        if (configJson == null || configJson.isEmpty()) {
            EmbellishChat.LOGGER.warn("{} json is invalid. Using default.", configJson);
            configJson = null;
        }

        if (styleJson == null || styleJson.isEmpty()) {
            EmbellishChat.LOGGER.warn("{} json is invalid. Using default.", styleJson);
            styleJson = null;
        }

        if (mentionJson == null || mentionJson.isEmpty()) {
            EmbellishChat.LOGGER.warn("{} json is invalid. Using default.", mentionJson);
            mentionJson = null;
        }

        if (presetJson == null || presetJson.isEmpty())
        {
            EmbellishChat.LOGGER.warn("{} json is invalid. Using default.", presetJson);
            presetJson = null;
        }

        if (playerJson == null || playerJson.isEmpty())
        {
            EmbellishChat.LOGGER.warn("{} json is invalid. Using default.", presetJson);
            presetJson = null;
        }

        try {
            Config config = gson.fromJson(configJson, Config.class);

            if (config != null && config.version() != null && config.version().equals(CONFIG.version())) {
                CONFIG = config;
                STYLE = gson.fromJson(styleJson, Style.class);
                MENTION = gson.fromJson(mentionJson, Mention.class);
                PRESET = gson.fromJson(presetJson, Preset.class);
                PLAYER = gson.fromJson(playerJson, Player.class);

                broadcastConfig();
                EmbellishChat.LOGGER.info("Config loaded successfully.");
                return true;
            } else {
                EmbellishChat.LOGGER.warn("Config version mismatch or invalid. Using default config.");
            }
        } catch (JsonSyntaxException e) {
            EmbellishChat.LOGGER.error("Failed to parse config. Using default values.", e);
        }

        broadcastConfig();
        return false;
    }

    private JsonObject readJsonFile(String file) {
        Path path = configDirPath.resolve(file);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (IOException | JsonSyntaxException e) {
            EmbellishChat.LOGGER.warn("Failed to read {}: {}", file, e.getMessage());
            return null;
        }
    }

    public void writeConfig() {
        writeConfig(this::writeJsonFile);
    }

    private void writeConfig(BiConsumer<String,JsonObject> writer) {
        synchronized (LOCK_KEY){
            if (writer == null) writer = this::writeJsonFile;
            writer.accept(Config.CONFIG_FILE_NAME,gson.toJsonTree(CONFIG).getAsJsonObject());
            writer.accept(Style.STYLE_FILE_NAME, gson.toJsonTree(STYLE).getAsJsonObject());
            writer.accept(Mention.MENTION_FILE_NAME, gson.toJsonTree(MENTION).getAsJsonObject());
            writer.accept(Preset.PRESET_FILE_NAME, gson.toJsonTree(PRESET).getAsJsonObject());
            writer.accept(Player.PLAYER_FILE_NAME,gson.toJsonTree(PLAYER).getAsJsonObject());
        }
    }

    private  void writeIfAbsent(String file, JsonObject json) {
        Path path = configDirPath.resolve(file);
        if (Files.exists(path)) return;
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        )) {
            gson.toJson(json, writer);
            EmbellishChat.LOGGER.info("Saved {}", file);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to write {}: {}", file, e.getMessage());
        }
    }

    private void writeJsonFile(String file, JsonObject json) {
        Path path = configDirPath.resolve(file);
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        )) {
            gson.toJson(json, writer);
            EmbellishChat.LOGGER.info("Saved {}", file);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to write {}: {}", file, e.getMessage());
        }
    }

    public void addListener(ConfigListener listener) {
        listeners.add(listener);
    }

    public void broadcastConfig() {
        for (ConfigListener listener : listeners) {
            listener.onConfigReload();
        }
    }
}
