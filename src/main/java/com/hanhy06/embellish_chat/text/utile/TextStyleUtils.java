package com.hanhy06.embellish_chat.text.utile;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Config;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TextStyleUtils {
    private static HashMap<String, Integer> colorPreset;

    private static final int URL_COLOR = 0x0000EE;

    public TextStyleUtils(Config config){
        colorPreset = config.colorPreset();
    }

    public static MutableText applyHexColor(MutableText text, String hex) {
        int color = Color.decode(hex).getRGB();
        return text.styled(style -> style.withColor(color));
    }

    public static MutableText applyPresetColor(MutableText text, String preset){
        int color = colorPreset.get(preset);
        return text.styled(style -> style.withColor(color));
    }

    public static MutableText applyRainbowColor(MutableText text,String option) {
        Runs runs = flatten(text);
        String string = runs.full();
        int length = string.length();
        if (length == 0) return text;

        MutableText out = Text.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, 0.7f, 1f);
            out.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return out;
    }

    public static MutableText applyFont(MutableText text, String strFont) {
        return text.styled(style -> style.withFont(new StyleSpriteSource.Font(Identifier.tryParse(strFont))));
    }

    public static MutableText applyURI(MutableText text, String strUri) {
        try {
            URI uri = URI.create(strUri);
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return text.styled(style -> style.withClickEvent(clickEvent));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL address: {}", strUri);
            return text;
        }
    }

    public static Runs flatten(Text text) {
        List<Run> list = new ArrayList<>();
        StringBuilder all = new StringBuilder();

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                int start = all.length();
                all.append(content);
                int end = all.length();
                list.add(new Run(start, end, style, content));
                return Optional.empty();
            }
        }, Style.EMPTY);

        return new Runs(all.toString(), list);
    }

    public static MutableText slice(Runs runs, int begin, int end) {
        MutableText out = Text.empty();
        for (Run run : runs.runs()) {
            if (run.end() <= begin) continue;
            if (run.start() >= end) break;

            int startIndex = Math.max(begin, run.start()) - run.start();
            int endIndex = Math.min(end, run.end()) - run.start();
            out.append(Text.literal(run.content().substring(startIndex, endIndex)).setStyle(run.style()));
        }
        return out;
    }
}
