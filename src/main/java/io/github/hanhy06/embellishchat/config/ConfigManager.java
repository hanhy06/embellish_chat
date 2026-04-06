package io.github.hanhy06.embellishchat.config;

import com.google.gson.*;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.*;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.styling.rule.StyleRule;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    private final List<ConfigListener> listeners = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pattern.class, new PatternTypeAdapter())
            .registerTypeAdapter(SoundEvent.class, new SoundEventTypeAdapter())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
            .registerTypeAdapter(MutableComponent.class, new MutableTextAdapter())
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
        JsonObject mergedConfig = gson.toJsonTree(defaultConfig).getAsJsonObject().deepCopy();

        for (String fileName : List.of(
                CONFIG_FILE_NAME,
                STYLE_FILE_NAME,
                MENTION_FILE_NAME,
                PRESET_FILE_NAME
        )) {
            JsonObject fileJson = readJsonFile(fileName);

            if (fileJson == null) {
                continue;
            }

            for (Map.Entry<String, JsonElement> entry : fileJson.entrySet()) {
                mergedConfig.add(entry.getKey(), entry.getValue().deepCopy());
            }
        }

        try {
            Config loadedConfig = gson.fromJson(mergedConfig, Config.class);

            if (loadedConfig == null) {
                EmbellishChat.LOGGER.warn("Config is empty or invalid. Keeping current config.");
            } else if (!Objects.equals(loadedConfig.version(), defaultConfig.version())) {
                EmbellishChat.LOGGER.warn("Config version mismatch. Keeping current config.");
            } else {
                String validationError = validateConfig(loadedConfig);
                if (validationError == null) {
                    config = loadedConfig;
                    broadcastConfig();
                    EmbellishChat.LOGGER.info("Config loaded successfully.");
                    return true;
                }

                EmbellishChat.LOGGER.warn("Config validation failed: {}. Keeping current config.", validationError);
            }
        } catch (RuntimeException e) {
            EmbellishChat.LOGGER.error("Failed to parse merged config. Keeping current config.", e);
        }

        broadcastConfig();
        return false;
    }

    private String validateConfig(Config config) {
        if (config.version() == null) return "version is missing";
        if (config.style_rules() == null) return "style_rules is missing";
        if (config.mention_rules() == null) return "mention_rules is missing";
        if (config.color() == null) return "color is missing";
        if (config.atlas() == null) return "atlas is missing";
        if (config.whitelist() == null) return "whitelist is missing";
        if (config.prefix() == null) return "prefix is missing";
        if (config.delimiter() == null) return "delimiter is missing";
        if (config.timestamp() == null) return "timestamp is missing";
        if (config.url_color() == null) return "url_color is missing";
        if (config.banned_players() == null) return "banned_players is missing";
        if (config.notify_off_players() == null) return "notify_off_players is missing";

        try {
            Pattern.compile(config.delimiter());
        } catch (PatternSyntaxException e) {
            return "delimiter is not a valid regex";
        }

        try {
            DateTimeFormatter.ofPattern(config.timestamp()).format(LocalDateTime.now());
        } catch (IllegalArgumentException e) {
            return "timestamp is not a valid date format";
        }

        for (Map.Entry<String, List<StyleRule>> entry : config.style_rules().entrySet()) {
            if (entry.getValue() == null) return "style_rules contains a null list";
            for (StyleRule rule : entry.getValue()) {
                if (rule == null) return "style_rules contains a null rule";
                if (rule.pattern() == null) return "style_rules contains a rule with null pattern";
                if (rule.pattern().matcher("").groupCount() < 2) {
                    return "style rule patterns must have at least two capture groups";
                }
                if (rule.styles() == null) return "style_rules contains a rule with null styles";
            }
        }

        for (Map.Entry<String, List<MentionRule>> entry : config.mention_rules().entrySet()) {
            if (entry.getValue() == null) return "mention_rules contains a null list";
            for (MentionRule rule : entry.getValue()) {
                if (rule == null) return "mention_rules contains a null rule";
                if (rule.pattern() == null) return "mention_rules contains a rule with null pattern";
                if (rule.pattern().matcher("").groupCount() < 1) {
                    return "mention rule patterns must have at least one capture group";
                }
                if (rule.mentions() == null) return "mention_rules contains a rule with null mentions";
                if (rule.mentions().isEmpty()) return "mention_rules contains a rule with empty mentions";
                if (rule.styles() == null) return "mention_rules contains a rule with null styles";
            }
        }

        for (Map.Entry<String, MutableComponent> entry : config.prefix().entrySet()) {
            if (entry.getValue() == null) return "prefix contains a null value";
        }

        return null;
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

    public void saveAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                writeConfig();
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to save config async", e);
            }
        });
    }

    private void writeConfig(BiConsumer<String,JsonObject> writer) {
        JsonObject fullJson;
        synchronized (LOCK_KEY){
            fullJson = gson.toJsonTree(config).getAsJsonObject();
        }

        JsonElement stylingRules = fullJson.remove("style_rules");
        JsonObject stylesJson = new JsonObject();
        if (stylingRules != null) {
            stylesJson.add("style_rules", stylingRules);
        }

        JsonElement mentionRules = fullJson.remove("mention_rules");
        JsonObject mentionsJson = new JsonObject();
        if (mentionRules != null) {
            mentionsJson.add("mention_rules", mentionRules);
        }

        JsonObject presetsJson = new JsonObject();
        JsonElement colorPreset = fullJson.remove("color");
        if (colorPreset != null) presetsJson.add("color", colorPreset);
        JsonElement atlasPreset = fullJson.remove("atlas");
        if (atlasPreset != null) presetsJson.add("atlas", atlasPreset);
        JsonElement whitelist = fullJson.remove("whitelist");
        if (whitelist != null) presetsJson.add("whitelist", whitelist);
        JsonElement prefixes = fullJson.remove("prefix");
        if (prefixes != null) presetsJson.add("prefix", prefixes);

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
