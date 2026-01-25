package io.github.hanhy06.embellishchat.config;

import com.google.gson.*;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.ColorTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.IdentifierTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.PatternTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.SoundEventTypeAdapter;
import net.minecraft.sound.SoundEvent;
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

    private static final String CONFIG_FILE_DIR = EmbellishChat.MOD_ID;
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String STYLE_FILE_NAME = "styles.json";
    private static final String MENTION_FILE_NAME = "mentions.json";

    private final Path configDirPath;
    private Config config = Config.createDefault();

    private final List<ConfigListener> listeners = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pattern.class, new PatternTypeAdapter())
            .registerTypeAdapter(SoundEvent.class, new SoundEventTypeAdapter())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .setPrettyPrinting()
            .setStrictness(Strictness.LENIENT)
            .disableHtmlEscaping()
            .create();

    public static Config getConfig() {
        return INSTANCE.config;
    }

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
        Config defaultConfig = Config.createDefault();

        JsonObject configJson = readJsonFile(CONFIG_FILE_NAME);
        JsonObject stylesJson = readJsonFile(STYLE_FILE_NAME);
        JsonObject mentionsJson = readJsonFile(MENTION_FILE_NAME);

        JsonObject merged = configJson != null ? configJson : new JsonObject();

        if (stylesJson != null && stylesJson.has("stylingRules")) {
            merged.add("stylingRules", stylesJson.get("stylingRules"));
        }
        if (mentionsJson != null && mentionsJson.has("mentionRules")) {
            merged.add("mentionRules", mentionsJson.get("mentionRules"));
        }

        try {
            Config loaded = gson.fromJson(merged, Config.class);

            if (loaded != null && loaded.version() != null && loaded.version().equals(defaultConfig.version())) {
                config = loaded;
                broadcastConfig();
                EmbellishChat.LOGGER.info("Config loaded successfully.");
                return true;
            } else {
                EmbellishChat.LOGGER.warn("Config version mismatch or invalid. Using default config.");
            }
        } catch (JsonSyntaxException e) {
            EmbellishChat.LOGGER.error("Failed to parse merged config. Using default values.", e);
        }

        config = defaultConfig;
        broadcastConfig();
        return false;
    }

    private JsonObject readJsonFile(String fileName) {
        Path filePath = configDirPath.resolve(fileName);
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (IOException | JsonSyntaxException e) {
            EmbellishChat.LOGGER.warn("Failed to read {}: {}", fileName, e.getMessage());
            return null;
        }
    }

    public void writeConfig(BiConsumer<String,JsonObject> writer) {
        JsonObject fullJson = gson.toJsonTree(config).getAsJsonObject();

        JsonElement stylingRules = fullJson.remove("stylingRules");
        JsonObject stylesJson = new JsonObject();
        if (stylingRules != null) {
            stylesJson.add("stylingRules", stylingRules);
        }

        JsonElement mentionRules = fullJson.remove("mentionRules");
        JsonObject mentionsJson = new JsonObject();
        if (mentionRules != null) {
            mentionsJson.add("mentionRules", mentionRules);
        }

        if (writer == null) writer = this::writeJsonFile;
        writer.accept(CONFIG_FILE_NAME,fullJson);
        writer.accept(STYLE_FILE_NAME, stylesJson);
        writer.accept(MENTION_FILE_NAME, mentionsJson);
    }

    private  void writeIfAbsent(String file, JsonObject json) {
        Path filePath = configDirPath.resolve(file);
        if (Files.exists(filePath)) return;
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        )) {
            gson.toJson(json, writer);
            EmbellishChat.LOGGER.info("Saved {}", file);
        } catch (IOException e) {
            EmbellishChat.LOGGER.error("Failed to write {}: {}", file, e.getMessage());
        }
    }

    private void writeJsonFile(String file, JsonObject json) {
        Path filePath = configDirPath.resolve(file);
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8,
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
            listener.onConfigReload(config);
        }
    }
}
