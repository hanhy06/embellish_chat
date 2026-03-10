package io.github.hanhy06.embellishchat.config.data;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

public record Config(
        String version,
        String delimiter,
        String timestamp,
        String command_alias,
        Color url_color,
        Color team_color,
        boolean notify_command_enabled,
        boolean notify_mention_enabled,
        boolean disable_vanilla_chat_format
)
{
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final Config DEFAULT = new Config(
            FabricLoader.getInstance()
                    .getModContainer(EmbellishChat.MOD_ID)
                    .orElseThrow()
                    .getMetadata()
                    .getVersion()
                    .getFriendlyString(),
            ",",
            "yyyy-MM-dd HH:mm:ss",
            "ec",
            new Color(0x0000EE),
            new Color(0xFF55FF),
            true,
            true,
            false
    );
}
