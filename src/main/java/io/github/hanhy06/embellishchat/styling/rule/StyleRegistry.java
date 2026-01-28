package io.github.hanhy06.embellishchat.styling.rule;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.discord.DiscordMessenger;
import io.github.hanhy06.embellishchat.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.styling.data.Runs;
import io.github.hanhy06.embellishchat.styling.util.BubbleUtil;
import io.github.hanhy06.embellishchat.styling.util.ColorUtil;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.text.object.PlayerTextObjectContents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

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
    private final HashMap<String, AtlasTextObjectContents> atlasPreset;
    private final EnumMap<StyleType, Function<StyleParameter, MutableText>> registers;
    private final Pattern HEX_CODE = Pattern.compile("#[A-Fa-f0-9]{6}");
    private final DiscordMessenger messenger;

    public StyleRegistry(Config config) {
        this.config = config;
        this.timestamp = DateTimeFormatter.ofPattern(config.timestamp());
        this.colorPreset = config.colorPreset();
        this.atlasPreset = config.atlasPreset();
        this.registers = new EnumMap<>(Map.ofEntries(
                entry(StyleType.COLOR_HEX, this::COLOR_HEX),
                entry(StyleType.COLOR_RAINBOW, this::COLOR_RAINBOW),
                entry(StyleType.COLOR_GRADIENT, this::COLOR_GRADIENT),
                entry(StyleType.COLOR_PRESET, this::COLOR_PRESET),
                entry(StyleType.COLOR_SHADOW, this::COLOR_SHADOW),
                entry(StyleType.COLOR_TEAM, this::COLOR_TEAM),

                entry(StyleType.BOLD, this::BOLD),
                entry(StyleType.ITALIC, this::ITALIC),
                entry(StyleType.UNDERLINE, this::UNDERLINE),
                entry(StyleType.STRIKETHROUGH, this::STRIKETHROUGH),
                entry(StyleType.OBFUSCATED, this::OBFUSCATED),
                entry(StyleType.FONT, this::FONT),
                entry(StyleType.CLEAR, this::CLEAR),

                entry(StyleType.CLICK_COMMAND_RUN, this::CLICK_COMMAND_RUN),
                entry(StyleType.CLICK_COMMAND_SUGGEST, this::CLICK_COMMAND_SUGGEST),
                entry(StyleType.CLICK_COPY, this::CLICK_COPY),
                entry(StyleType.HOVER_TEXT, this::HOVER_TEXT),
                entry(StyleType.HOVER_ITEM, this::HOVER_ITEM),
                entry(StyleType.URL, this::URL),
                entry(StyleType.METADATA, this::METADATA),

                entry(StyleType.UPPER, this::UPPER),
                entry(StyleType.LOWER, this::LOWER),
                entry(StyleType.CAPITALIZE, this::CAPITALIZE),
                entry(StyleType.REPLACE, this::REPLACE),
                entry(StyleType.MASK, this::MASK),
                entry(StyleType.PREFIX, this::PREFIX),
                entry(StyleType.SUFFIX, this::SUFFIX),

                entry(StyleType.SHOW_ITEM, this::SHOW_ITEM),
                entry(StyleType.SHOW_INVENTORY, this::SHOW_INVENTORY),
                entry(StyleType.SHOW_ENDER_CHEST, this::SHOW_ENDER_CHEST),
                entry(StyleType.ATLAS_PRESET, this::ATLAS_PRESET),
                entry(StyleType.JSON, this::JSON),
                entry(StyleType.DISCORD_JSON, this::DISCORD_JSON),
                entry(StyleType.COMMAND_RUN, this::COMMAND_RUN),
                entry(StyleType.LOG, this::LOG),
                entry(StyleType.BUBBLE, this::BUBBLE),
                entry(StyleType.BLOCK, this::BLOCK)
        ));
        this.messenger = new DiscordMessenger(config);
    }

    public Function<StyleParameter, MutableText> get(StyleType styleType) {
        return registers.get(styleType);
    }

    public MutableText COLOR_HEX(StyleParameter parameter) {
        int color = 0xffffff;

        try {
            color = Color.decode(parameter.getString()).getRGB();
        } catch (NumberFormatException e) {
            EmbellishChat.LOGGER.warn("Invalid hex color format: {}", parameter.getString());
        } catch (NullPointerException e){
            EmbellishChat.LOGGER.warn("The option value you entered is null.");
        }

        return parameter.segment().fillStyle(Style.EMPTY.withColor(color));
    }

    public MutableText COLOR_RAINBOW(StyleParameter parameter) {
        Runs runs = flatten(parameter.segment());
        String string = runs.full();
        int length = string.length();
        float saturation = 0.7f;

        try {
            saturation = Float.parseFloat(parameter.getString());
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
        Matcher matcher = HEX_CODE.matcher(parameter.getString());
        if (!matcher.find()) {
            return parameter.segment();
        }

        List<Color> colors = new ArrayList<>();
        String string = parameter.segment().getString();
        int length = string.length();

        do {
            colors.add(Color.decode(matcher.group()));
        } while (matcher.find());

        if (colors.size() < 2 || length < colors.size()) {
            return parameter.segment().fillStyle(Style.EMPTY.withColor(colors.getFirst().getRGB()));
        }

        Runs runs = flatten(parameter.segment());
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
        Color color = colorPreset.getOrDefault(parameter.getString(), Color.WHITE);
        return parameter.segment().fillStyle(Style.EMPTY.withColor(color.getRGB()));
    }

    public MutableText COLOR_SHADOW(StyleParameter parameter) {
        int color = 0xffffff;

        try {
            color = Color.decode(parameter.getString()).getRGB();
        } catch (NumberFormatException e) {
            EmbellishChat.LOGGER.warn("Invalid hex color format: {}", parameter.getString());
        } catch (NullPointerException e){
            EmbellishChat.LOGGER.warn("The option value you entered is null.");
        }

        return parameter.segment().fillStyle(Style.EMPTY.withShadowColor(color));
    }

    public MutableText COLOR_TEAM(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();
        Team team = player.getScoreboardTeam();
        if (team == null) return parameter.segment();

        Formatting formatting = team.getColor();
        if (formatting != null && formatting.isColor()){
            return parameter.segment().fillStyle(Style.EMPTY.withColor(formatting));
        }

        return parameter.segment();
    }

    public MutableText BOLD(StyleParameter parameter) {
        return parameter.segment().fillStyle(Style.EMPTY.withBold(true));
    }

    public MutableText ITALIC(StyleParameter parameter) {
        return parameter.segment().fillStyle(Style.EMPTY.withItalic(true));
    }

    public MutableText UNDERLINE(StyleParameter parameter) {
        return parameter.segment().fillStyle(Style.EMPTY.withUnderline(true));
    }

    public MutableText STRIKETHROUGH(StyleParameter parameter) {
        return parameter.segment().fillStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableText OBFUSCATED(StyleParameter parameter) {
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(parameter.segment().getString()));
        return parameter.segment().fillStyle(Style.EMPTY.withObfuscated(true).withHoverEvent(hoverEvent));
    }

    public MutableText FONT(StyleParameter parameter) {
        StyleSpriteSource font = new StyleSpriteSource.Font(Identifier.tryParse(parameter.getString()));
        return parameter.segment().fillStyle(Style.EMPTY.withFont(font));
    }

    public MutableText CLEAR(StyleParameter parameter) {
        return Text.literal(parameter.segment().getString());
    }

    public MutableText CLICK_COMMAND_RUN(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.RunCommand(parameter.getString());
        return parameter.segment().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText CLICK_COMMAND_SUGGEST(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.SuggestCommand(parameter.getString());
        return parameter.segment().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText CLICK_COPY(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(parameter.getString());
        return parameter.segment().fillStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableText HOVER_TEXT(StyleParameter parameter){
        HoverEvent hoverEvent = new HoverEvent.ShowText(parameter.getText());
        return parameter.segment().fillStyle(Style.EMPTY.withHoverEvent(hoverEvent));
    }

    public MutableText HOVER_ITEM(StyleParameter parameter){
        ItemStack item;
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();

        int slot;
        try {slot = Integer.decode(parameter.getString());}
        catch (NumberFormatException e) {slot = -1;}

        if (slot < 0 || 42 < slot) item = player.getMainHandStack();
        else item = player.getInventory().getStack(slot);
        if (item.isEmpty()) return parameter.segment();

        HoverEvent hoverEvent = new HoverEvent.ShowItem(item);
        return parameter.segment().fillStyle(Style.EMPTY.withHoverEvent(hoverEvent));
    }

    public MutableText URL(StyleParameter parameter) {
        try {
            URI uri = URI.create(parameter.getString());
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return parameter.segment().fillStyle(Style.EMPTY
                    .withClickEvent(clickEvent)
                    .withColor(config.urlColor().getRGB()));
        } catch (IllegalArgumentException e) {
            EmbellishChat.LOGGER.warn("Invalid URL provided for segment [{}]: {}", parameter.segment().getString(), parameter.getString());
            return parameter.segment();
        }
    }

    public MutableText METADATA(StyleParameter parameter) {
        MutableText text = parameter.segment();
        String now = LocalDateTime.now().format(timestamp);

        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(now + "\nClick to copy to clipboard").formatted(Formatting.GRAY));
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(now + " " + text.getString());

        return text.fillStyle(Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
    }

    public MutableText UPPER(StyleParameter parameter) {
        String string = parameter.segment().getString();
        return Text.literal(string.toUpperCase()).fillStyle(parameter.segment().getStyle());
    }

    public MutableText LOWER(StyleParameter parameter) {
        String string = parameter.segment().getString();
        return Text.literal(string.toLowerCase()).fillStyle(parameter.segment().getStyle());
    }

    public MutableText CAPITALIZE(StyleParameter parameter){
        String string = parameter.segment().getString();
        return Text.literal(StringUtils.capitalize(string)).fillStyle(parameter.segment().getStyle());
    }

    public MutableText REPLACE(StyleParameter parameter) {
        return parameter.getText().copy().fillStyle(parameter.segment().getStyle());
    }

    public MutableText MASK(StyleParameter parameter) {
        int length = parameter.segment().getString().length();
        return Text.literal(parameter.getString().repeat(length)).fillStyle(parameter.segment().getStyle());
    }

    public MutableText PREFIX(StyleParameter parameter){
        MutableText prefix = parameter.option().copy();
        return prefix.append(parameter.segment());
    }

    public MutableText SUFFIX(StyleParameter parameter){
        MutableText suffix = parameter.getText().copy();
        return parameter.segment().append(suffix);
    }

    public MutableText SHOW_ITEM(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();
        ItemStack item = player.getMainHandStack();
        if (item == null || item.isEmpty()) return parameter.segment();

        AtlasTextObjectContents atlas;
        if (!parameter.getString().isBlank()){
            List<String> segments = OptionUtil.split(parameter.getString().trim(),";");
            atlas = new AtlasTextObjectContents(Identifier.of(segments.getFirst()),Identifier.of(segments.getLast()));
        }else {
            String type = (item.getItem() instanceof BlockItem) ? "block" : "item";
            Identifier modelId = item.get(DataComponentTypes.ITEM_MODEL);
            if (modelId==null) return parameter.segment();

            atlas = new AtlasTextObjectContents(
                    Identifier.of(modelId.getNamespace(),type+"s"),
                    Identifier.of(modelId.getNamespace(),String.format("%s/%s",type,modelId.getPath()))
            );
        }

        Text text = Text.object(atlas);
        HoverEvent hoverEvent = new HoverEvent.ShowItem(item);
        ClickEvent clickEvent = new ClickEvent.RunCommand("/ec open "+player.getUuid());
        InventoryManager.putItem(player,item);

        return text.copy().fillStyle(Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
    }

    public MutableText SHOW_INVENTORY(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();
        InventoryManager.putInventory(player);

        ClickEvent clickEvent = new ClickEvent.RunCommand("/ec open "+player.getUuid());
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(player.getName().getString() + "'s inventory"));
        ProfileComponent component = ProfileComponent.ofStatic(player.getGameProfile());
        MutableText text = Text.object(new PlayerTextObjectContents(component, true));

        return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withHoverEvent(hoverEvent));
    }

    public MutableText SHOW_ENDER_CHEST(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();
        InventoryManager.putEnderChest(player);

        ClickEvent clickEvent = new ClickEvent.RunCommand("/ec open "+player.getUuid());
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(player.getName().getString() + "'s ender chest"));
        ProfileComponent component = ProfileComponent.ofStatic(player.getGameProfile());
        MutableText text = Text.object(new PlayerTextObjectContents(component, true));

        return text.fillStyle(Style.EMPTY.withClickEvent(clickEvent).withHoverEvent(hoverEvent));
    }

    public MutableText ATLAS_PRESET(StyleParameter parameter) {
        AtlasTextObjectContents atlas = atlasPreset.get(parameter.getString());
        if (atlas!=null) return Text.object(atlas);
        else return parameter.segment();
    }

    public MutableText JSON(StyleParameter parameter){
        JsonElement element = JsonParser.parseString(parameter.getString());
        Text text = TextCodecs.CODEC.parse(JsonOps.INSTANCE,element).getOrThrow();
        return text.copy();
    }

    public MutableText DISCORD_JSON(StyleParameter parameter){
        messenger.send(parameter.getString());
        return parameter.segment();
    }

    public MutableText COMMAND_RUN(StyleParameter parameter){
        ServerPlayerEntity player = parameter.player();
        if (player == null) return parameter.segment();
        MinecraftServer server = player.getCommandSource().getServer();
        String command = parameter.getString();
        if (command.startsWith("/")) command = command.substring(1);

        if (server != null) {
            ServerCommandSource commandSource = player.getCommandSource();
            try {
                server.getCommandManager().getDispatcher().execute(command, commandSource);
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to execute command: {}", command, e);
            }
        }

        return parameter.segment();
    }

    public MutableText LOG(StyleParameter parameter){
        EmbellishChat.LOGGER.info("Log StyleType segment: {}, open segment:{}, sender: {}",parameter.segment().getString(),parameter.getString(),parameter.player());
        return parameter.segment();
    }

    public MutableText BUBBLE(StyleParameter parameter){
        if (parameter.player() == null) return parameter.segment();
        BubbleUtil.spawnDisplayEntity(parameter.player(),parameter.segment());
        return parameter.segment();
    }

    public MutableText BLOCK(StyleParameter parameter) {
        throw new MessageProcessor.MessageBlockedException();
    }
}