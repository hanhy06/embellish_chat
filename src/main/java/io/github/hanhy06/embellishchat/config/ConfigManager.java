package io.github.hanhy06.embellishchat.config;

import com.google.gson.*;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.ColorTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.IdentifierTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.PatternTypeAdapter;
import io.github.hanhy06.embellishchat.config.adapter.SoundEventTypeAdapter;
import io.github.hanhy06.embellishchat.config.configs.*;
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
    public final Object LOCK_KEY = new Object();

    private static final String CONFIG_FILE_DIR = EmbellishChat.MOD_ID;

    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String STYLE_FILE_NAME = "styles.json";
    private static final String MENTION_FILE_NAME = "mentions.json";
    private static final String PRESET_FILE_NAME = "presets.json";

    private final Path configDirPath;

    private Config config = Config.createDefault();
    private MentionConfig mentions = MentionConfig.createDefault();
    private StyleConfig styles = StyleConfig.createDefault();
    private PresetConfig presets = PresetConfig.createDefault();
    private PlayerConfig players = PlayerConfig.createDefault();

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
        JsonObject defaultConfig = gson.toJsonTree(config).getAsJsonObject();
                
        JsonObject configJson = readJsonFile(CONFIG_FILE_NAME);
        JsonObject stylesJson = readJsonFile(STYLE_FILE_NAME);
        JsonObject mentionsJson = readJsonFile(MENTION_FILE_NAME);
        JsonObject presetsJson = readJsonFile(PRESET_FILE_NAME);

        JsonObject merged = configJson != null ? configJson : new JsonObject();

        if (stylesJson != null && stylesJson.has("STYLE_RULES")) {
            merged.add("STYLE_RULES", stylesJson.get("STYLE_RULES"));
        }else {
            merged.add("STYLE_RULES", defaultConfig.get("STYLE_RULES"));
        }
        if (mentionsJson != null && mentionsJson.has("MENTION_RULES")) {
            merged.add("MENTION_RULES", mentionsJson.get("MENTION_RULES"));
        }else{
            merged.add("MENTION_RULES", defaultConfig.get("MENTION_RULES"));
        }
        if (presetsJson != null) {
            if (presetsJson.has("COLOR")) {
                merged.add("COLOR", presetsJson.get("COLOR"));
            } else {
                merged.add("COLOR", defaultConfig.get("COLOR"));
            }
            if (presetsJson.has("ATLAS")) {
                merged.add("ATLAS", presetsJson.get("ATLAS"));
            } else {
                merged.add("ATLAS", defaultConfig.get("ATLAS"));
            }
            if (presetsJson.has("WHITELIST")) {
                merged.add("WHITELIST", presetsJson.get("WHITELIST"));
            } else {
                merged.add("WHITELIST", defaultConfig.get("WHITELIST"));
            }
            if (presetsJson.has("PREFIX")) {
                merged.add("PREFIX", presetsJson.get("PREFIX"));
            } else {
                merged.add("PREFIX", defaultConfig.get("PREFIX"));
            }
        } else {
            merged.add("COLOR", defaultConfig.get("COLOR"));
            merged.add("ATLAS", defaultConfig.get("ATLAS"));
            merged.add("WHITELIST", defaultConfig.get("WHITELIST"));
            merged.add("PREFIX", defaultConfig.get("PREFIX"));
        }

        try {
            Config loaded = gson.fromJson(merged, Config.class);

            if (loaded != null && loaded.VERSION() != null && loaded.VERSION().equals(config.VERSION())) {
                config = loaded;
                broadcastConfig();
                EmbellishChat.LOGGER.info("Config loaded successfully.");
                return true;
            } else {
                EmbellishChat.LOGGER.warn("Config VERSION mismatch or invalid. Using default config.");
            }
        } catch (JsonSyntaxException e) {
            EmbellishChat.LOGGER.error("Failed to parse merged config. Using default values.", e);
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
        JsonObject fullJson;
        synchronized (LOCK_KEY){
            fullJson = gson.toJsonTree(config).getAsJsonObject();
        }

        JsonElement stylingRules = fullJson.remove("STYLE_RULES");
        JsonObject stylesJson = new JsonObject();
        if (stylingRules != null) {
            stylesJson.add("STYLE_RULES", stylingRules);
        }

        JsonElement mentionRules = fullJson.remove("MENTION_RULES");
        JsonObject mentionsJson = new JsonObject();
        if (mentionRules != null) {
            mentionsJson.add("MENTION_RULES", mentionRules);
        }

        JsonObject presetsJson = new JsonObject();
        JsonElement colorPreset = fullJson.remove("COLOR");
        if (colorPreset != null) presetsJson.add("COLOR", colorPreset);
        JsonElement atlasPreset = fullJson.remove("ATLAS");
        if (atlasPreset != null) presetsJson.add("ATLAS", atlasPreset);
        JsonElement whitelist = fullJson.remove("WHITELIST");
        if (whitelist != null) presetsJson.add("WHITELIST", whitelist);
        JsonElement prefixes = fullJson.remove("PREFIX");
        if (prefixes != null) presetsJson.add("PREFIX", prefixes);

        if (writer == null) writer = this::writeJsonFile;
        writer.accept(CONFIG_FILE_NAME,fullJson);
        writer.accept(STYLE_FILE_NAME, stylesJson);
        writer.accept(MENTION_FILE_NAME, mentionsJson);
        writer.accept(PRESET_FILE_NAME, presetsJson);
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
            listener.onConfigReload(config);
        }
    }
}
