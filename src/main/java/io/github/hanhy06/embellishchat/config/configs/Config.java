package io.github.hanhy06.embellishchat.config.configs;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.*;
import java.util.List;

import static java.util.Map.entry;

public record Config(
        String VERSION,

        String DELIMITER,
        String TIMESTAMP,
        String COMMAND_ALIAS,
        Color URL_COLOR,
        Color TEAM_COLOR,
        boolean NOTIFY_COMMAND_ENABLED,
        boolean NOTIFY_MENTION_ENABLED,
        boolean DISABLE_VANILLA_CHAT_FORMAT,
        URI DISCORD_WEBHOOK
)
{
    public static Config createDefault(){
        return new Config(
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
                false,
                URI.create("")
        );
    }
}