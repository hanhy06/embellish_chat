package io.github.hanhy06.embellishchat.config;

import com.google.gson.*;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.adapter.*;
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
import java.util.*;
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
        JsonObject mergedConfig = gson.toJsonTree(config).getAsJsonObject().deepCopy();

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

            Deque<JsonObject[]> stack = new ArrayDeque<>();
            stack.push(new JsonObject[]{mergedConfig, fileJson});

            while (!stack.isEmpty()) {
                JsonObject[] current = stack.pop();
                JsonObject target = current[0];
                JsonObject source = current[1];

                for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
                    String key = entry.getKey();
                    JsonElement sourceValue = entry.getValue();

                    if (target.has(key)) {
                        JsonElement targetValue = target.get(key);

                        if (targetValue.isJsonObject() && sourceValue.isJsonObject()) {
                            stack.push(new JsonObject[]{
                                    targetValue.getAsJsonObject(),
                                    sourceValue.getAsJsonObject()
                            });
                            continue;
                        }
                    }

                    target.add(key, sourceValue.deepCopy());
                }
            }
        }

        try {
            Config loadedConfig = gson.fromJson(mergedConfig, Config.class);

            if (loadedConfig != null && Objects.equals(loadedConfig.version(), config.version())) {
                config = loadedConfig;
                broadcastConfig();
                EmbellishChat.LOGGER.info("Config loaded successfully.");
                return true;
            }

            EmbellishChat.LOGGER.warn("Config version mismatch or invalid. Using default config.");
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
