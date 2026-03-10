package io.github.hanhy06.embellishchat.config.data;

import io.github.hanhy06.embellishchat.config.ConfigInterface;
import net.minecraft.text.MutableText;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.entry;

public record Preset(
        HashMap<String, Color> color,
        HashMap<String, AtlasTextObjectContents> atlas,
        HashSet<URI> whitelist,
        LinkedHashMap<String, MutableText> prefix
) implements ConfigInterface {
    @Override
    public String getFileName() {
        return "presets.json";
    }

    @Override
    public ConfigInterface getDefault() {
        return new Preset(
                new HashMap<>(Map.ofEntries(
                        entry("black", new Color(0x000000)),
                        entry("dark blue", new Color(0x0000AA)),
                        entry("dark green", new Color(0x00AA00)),
                        entry("dark aqua", new Color(0x00AAAA)),
                        entry("dark red", new Color(0xAA0000)),
                        entry("dark purple", new Color(0xAA00AA)),
                        entry("gold", new Color(0xFFAA00)),
                        entry("gray", new Color(0xAAAAAA)),
                        entry("dark gray", new Color(0x555555)),
                        entry("blue", new Color(0x5555FF)),
                        entry("green", new Color(0x55FF55)),
                        entry("aqua", new Color(0x55FFFF)),
                        entry("red", new Color(0xFF5555)),
                        entry("light purple", new Color(0xFF55FF)),
                        entry("yellow", new Color(0xFFFF55)),
                        entry("white", new Color(0xFFFFFF))
                )),
                new HashMap<>(Map.ofEntries(
                        entry("fire", new AtlasTextObjectContents(
                                Identifier.of("minecraft:blocks"),Identifier.of("minecraft:block/campfire_fire"))
                        ),
                        entry("hunger", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:hud/food_full"))
                        ),
                        entry("heart", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:hud/heart/full"))
                        ),
                        entry("yes", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:container/beacon/confirm"))
                        ),
                        entry("no", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:container/beacon/cancel"))
                        ),
                        entry("move", new AtlasTextObjectContents(
                                Identifier.of("minecraft:gui"),Identifier.of("minecraft:mob_effect/wind_charged"))
                        )
                )),
                new HashSet<>(),
                new LinkedHashMap<>()
        );
    }
}
