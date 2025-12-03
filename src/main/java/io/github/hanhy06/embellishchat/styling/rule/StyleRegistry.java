package io.github.hanhy06.embellishchat.styling.rule;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.styling.util.Runs;
import io.github.hanhy06.embellishchat.util.ColorUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.flatten;
import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.slice;
import static java.util.Map.entry;

public class StyleRegistry {
    private final Config config;
    private final DateTimeFormatter timestamp;
    private final HashMap<String, Color> colorPreset;
    private final EnumMap<StyleType, Function<StyleParameter, MutableText>> registers;
    private final Pattern HEX_CODE = Pattern.compile("#[A-Fa-f0-9]{6}");

    public StyleRegistry(Config config) {
        this.config = config;
        this.timestamp = DateTimeFormatter.ofPattern(config.timestamp());
        this.colorPreset = config.colorPreset();
        this.registers = new EnumMap<>(Map.ofEntries(
                entry(StyleType.METADATA, this::METADATA),
                entry(StyleType.LOG, this::LOG),
                entry(StyleType.COLOR_HEX, this::COLOR_HEX),
                entry(StyleType.COLOR_RAINBOW, this::COLOR_RAINBOW),
                entry(StyleType.COLOR_GRADIENT, this::COLOR_GRADIENT),
                entry(StyleType.COLOR_PRESET, this::COLOR_PRESET),
                entry(StyleType.COLOR_SHADOW, this::COLOR_SHADOW),
                entry(StyleType.COLOR_TEAM, this::COLOR_TEAM),
                entry(StyleType.COMMAND_RUN, this::COMMAND_RUN),
                entry(StyleType.CLICK_COMMAND_RUN, this::CLICK_COMMAND_RUN),
                entry(StyleType.CLICK_COMMAND_SUGGEST, this::CLICK_COMMAND_SUGGEST),
                entry(StyleType.CLICK_COPY, this::CLICK_COPY),
                entry(StyleType.HOVER_TEXT,this::HOVER_TEXT),
                entry(StyleType.HOVER_ITEM,this::HOVER_ITEM),
                entry(StyleType.FONT, this::FONT),
                entry(StyleType.URL, this::URL),
                entry(StyleType.BOLD, this::BOLD),
                entry(StyleType.ITALIC, this::ITALIC),
                entry(StyleType.UNDERLINE, this::UNDERLINE),
                entry(StyleType.STRIKETHROUGH, this::STRIKETHROUGH),
                entry(StyleType.OBFUSCATED, this::OBFUSCATED),
                entry(StyleType.REPLACE, this::REPLACE),
                entry(StyleType.MASK, this::MASK),
                entry(StyleType.UPPER, this::UPPER),
                entry(StyleType.LOWER, this::LOWER),
                entry(StyleType.JSON, this::JSON)
        ));
    }

    public Function<StyleParameter, MutableText> get(StyleType styleType) {
        return registers.get(styleType);
    }

    public MutableText METADATA(StyleParameter parameter) {
        MutableText text = parameter.text();
        String now = LocalDateTime.now().format(timestamp);

        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(now + "\nClick to copy to clipboard").formatted(Formatting.GRAY));
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(now + " " + text.getString());

        return text.fillStyle(Style.EMPTY
                .withHoverEvent(hoverEvent)
                .withClickEvent(clickEvent));
    }

    public MutableText LOG(StyleParameter parameter){
        EmbellishChat.LOGGER.info("Log StyleType text: {}, sender: {}",parameter.text().getString(),parameter.player());
        return parameter.text();
    }

    public MutableText COLOR_HEX(StyleParameter parameter) {
        int color = 0xffffff;

        try {
            color = Color.decode(parameter.option()).getRGB();
        } catch (NumberFormatException e) {
            EmbellishChat.LOGGER.warn("Invalid hex color format: {}", parameter.option());
        } catch (NullPointerException e){
            EmbellishChat.LOGGER.warn("The option value you entered is null.");
        }

        return parameter.text().fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_RAINBOW(StyleParameter parameter) {
        Runs runs = flatten(parameter.text());
        String string = runs.full();
        int length = string.length();
        float saturation = 0.7f;

        try {
            saturation = Float.parseFloat(parameter.option());
        } catch (NumberFormatException e) {
            EmbellishChat.LOGGER.warn("The option value you entered is not a valid number.");
        } catch (NullPointerException e){
            EmbellishChat.LOGGER.warn("The option value you entered is null.");
        }

        MutableText result = Text.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, saturation, 1f);
            result.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(rgb)));
        }
        return result;
    }

    public MutableText COLOR_GRADIENT(StyleParameter parameter) {
        Matcher matcher = HEX_CODE.matcher(parameter.option());
        if (!matcher.find()) {
            return parameter.text();
        }

        List<Color> colors = new ArrayList<>();
        String string = parameter.text().getString();
        int length = string.length();

        do {
            colors.add(Color.decode(matcher.group()));
        } while (matcher.find());

        if (colors.size() < 2 || length < colors.size()) {
            return parameter.text().fillStyle(Style.EMPTY.withColor(colors.getFirst().getRGB()));
        }

        Runs runs = flatten(parameter.text());
        MutableText result = Text.empty();
        int colorCount = colors.size();

        for (int i = 0; i < length; i++) {
            float position = (float) i / (length - 1);
            float scaledPosition = position * (colorCount - 1);
            int segmentIndex = Math.min((int) scaledPosition, colorCount - 2);
            float t = scaledPosition - segmentIndex;

            Color interpolated = ColorUtil.lerpColor(
                    colors.get(segmentIndex),
                    colors.get(segmentIndex + 1),
                    t
            );

            result.append(slice(runs, i, i + 1).fillStyle(Style.EMPTY.withColor(interpolated.getRGB())));
        }

        return result;
    }

    public MutableText COLOR_PRESET(StyleParameter parameter) {
        Color color = colorPreset.getOrDefault(parameter.option(), Color.WHITE);
        return parameter.text().fillStyle(Style.EMPTY.withColor(color.getRGB()));
    }

    public MutableText COLOR_SHADOW(StyleParameter parameter) {
        int color = 0xffffff;

        try {
            color = Color.decode(parameter.option()).getRGB();
        } catch (NumberFormatException e) {
            EmbellishChat.LOGGER.warn("Invalid hex color format: {}", parameter.option());
        } catch (NullPointerException e){
            EmbellishChat.LOGGER.warn("The option value you entered is null.");
        }

        return parameter.text().fillStyle(Style.EMPTY.withShadowColor(color));
    }

    public MutableText COLOR_TEAM(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        Team team = player.getScoreboardTeam();

        if (team == null) return parameter.text();

        Formatting formatting = team.getColor();
        if (formatting != null && formatting.isColor()){
            return parameter.text().fillStyle(Style.EMPTY.withColor(formatting));
        }

        return parameter.text();
    }

    public MutableText COMMAND_RUN(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        MinecraftServer server = player.getCommandSource().getServer();
        String command = parameter.option();
        if (command.startsWith("/")) command = command.substring(1);

        if (server != null) {
            ServerCommandSource commandSource = player.getCommandSource();
            try {
                server.getCommandManager().getDispatcher().execute(command, commandSource);
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to execute command: {}", command, e);
            }
        }

        return parameter.text();
    }

    public MutableText CLICK_COMMAND_RUN(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.RunCommand(parameter.option());
        return parameter.text().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText CLICK_COMMAND_SUGGEST(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.SuggestCommand(parameter.option());
        return parameter.text().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText CLICK_COPY(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(parameter.option());
        return parameter.text().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText HOVER_TEXT(StyleParameter parameter){
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(parameter.option()));
        return parameter.text().fillStyle(Style.EMPTY.withHoverEvent(hoverEvent));
    }

    public MutableText HOVER_ITEM(StyleParameter parameter){
        ItemStack handItem = parameter.player().getMainHandStack();

        if (handItem.isEmpty()){
            return parameter.text();
        }else {
            HoverEvent hoverEvent = new HoverEvent.ShowItem(handItem);
            return parameter.text().fillStyle(Style.EMPTY.withHoverEvent(hoverEvent));
        }
    }

    public MutableText FONT(StyleParameter parameter) {
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(parameter.option()));
        return parameter.text().fillStyle(Style.EMPTY.withFont(font));
    }

    public MutableText URL(StyleParameter parameter) {
        try {
            URI uri = URI.create(parameter.option());
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return parameter.text().fillStyle(Style.EMPTY
                    .withClickEvent(clickEvent)
                    .withColor(config.urlColor().getRGB()));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL provided for text [{}]: {}", parameter.text().getString(), parameter.option());
            return parameter.text();
        }
    }

    public MutableText BOLD(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withBold(true));
    }

    public MutableText ITALIC(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withItalic(true));
    }

    public MutableText UNDERLINE(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withUnderline(true));
    }

    public MutableText OBFUSCATED(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withObfuscated(true));
    }

    public MutableText STRIKETHROUGH(StyleParameter parameter) {
        return parameter.text().fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableText REPLACE(StyleParameter parameter) {
        return Text.of(parameter.option()).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText MASK(StyleParameter parameter) {
        int length = parameter.text().getString().length();
        return Text.of(parameter.option().repeat(length)).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText UPPER(StyleParameter parameter) {
        String string = parameter.text().getString();
        return Text.of(string.toUpperCase()).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText LOWER(StyleParameter parameter) {
        String string = parameter.text().getString();
        return Text.of(string.toLowerCase()).copy().fillStyle(parameter.text().getStyle());
    }

    public MutableText JSON(StyleParameter parameter){
        JsonElement element = JsonParser.parseString(parameter.option());
        Text text = TextCodecs.CODEC.parse(JsonOps.INSTANCE,element).getOrThrow();
        return text.copy();
    }
}