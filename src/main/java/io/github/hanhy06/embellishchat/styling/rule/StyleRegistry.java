package io.github.hanhy06.embellishchat.styling.rule;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.screen.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.styling.data.Runs;
import io.github.hanhy06.embellishchat.styling.util.BubbleUtil;
import io.github.hanhy06.embellishchat.styling.util.ColorUtil;
import io.github.hanhy06.embellishchat.styling.util.DiscordUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.scores.PlayerTeam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
    private final HashMap<String, AtlasSprite> icon;
    private final HashMap<String, AtlasSprite> item;
    private final HashMap<String, Color> color;
    private final HashSet<String> whitelist;

    private final EnumMap<StyleType, Function<StyleParameter, MutableComponent>> registries;
    private final Pattern HEX_CODE = Pattern.compile("#[A-Fa-f0-9]{6}");
    private final DiscordUtil messenger;

    public StyleRegistry(Config config) {
        this.config = config;
        this.timestamp = DateTimeFormatter.ofPattern(config.timestamp());
        this.icon = config.icon();
        this.item = config.item();
        this.color = config.color();
        this.whitelist = config.whitelist();
        this.registries = new EnumMap<>(Map.ofEntries(
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
                entry(StyleType.ICON_PRESET, this::ICON_PRESET),
                entry(StyleType.JSON, this::JSON),
                entry(StyleType.DISCORD_JSON, this::DISCORD_JSON),
                entry(StyleType.COMMAND_RUN, this::COMMAND_RUN),
                entry(StyleType.LOG, this::LOG),
                entry(StyleType.BUBBLE, this::BUBBLE),
                entry(StyleType.BLOCK, this::BLOCK)
        ));
        this.messenger = new DiscordUtil();
    }

    public MutableComponent apply(StyleType styleType,StyleParameter parameter){
        Function<StyleParameter, MutableComponent> function = registries.get(styleType);
        try {
            return function.apply(parameter);
        } catch (MessageProcessor.MessageBlockedException block) {
            throw block;
        } catch (RuntimeException e) {
            EmbellishChat.LOGGER.warn("Failed to apply style [{}] with option [{}]", styleType, parameter.getString(), e);
            return parameter.segment();
        }
    }

    public MutableComponent COLOR_HEX(StyleParameter parameter) {
        int color = Color.decode(parameter.getString()).getRGB();
        return parameter.segment().withStyle(Style.EMPTY.withColor(color));
    }

    public MutableComponent COLOR_RAINBOW(StyleParameter parameter) {
        Runs runs = flatten(parameter.segment());
        String string = runs.full();
        int length = string.length();
        float saturation = NumberUtils.toFloat(parameter.getString(),0.7f);

        MutableComponent result = Component.empty();
        for (int i = 0; i < length; i++) {
            float hue = (float) i / length;
            int rgb = Color.HSBtoRGB(hue, saturation, 1f);
            result.append(slice(runs, i, i + 1).withStyle(Style.EMPTY.withColor(rgb)));
        }
        return result;
    }

    public MutableComponent COLOR_GRADIENT(StyleParameter parameter) {
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
            return parameter.segment().withStyle(Style.EMPTY.withColor(colors.getFirst().getRGB()));
        }

        Runs runs = flatten(parameter.segment());
        MutableComponent result = Component.empty();
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

            result.append(slice(runs, i, i + 1).withStyle(Style.EMPTY.withColor(interpolated.getRGB())));
        }

        return result;
    }

    public MutableComponent COLOR_PRESET(StyleParameter parameter) {
        Color color = this.color.getOrDefault(parameter.getString(), Color.WHITE);
        return parameter.segment().withStyle(Style.EMPTY.withColor(color.getRGB()));
    }

    public MutableComponent COLOR_SHADOW(StyleParameter parameter) {
        int color = Color.decode(parameter.getString()).getRGB();
        return parameter.segment().withStyle(Style.EMPTY.withShadowColor(color));
    }

    public MutableComponent COLOR_TEAM(StyleParameter parameter){
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();
        PlayerTeam team = player.getTeam();
        if (team == null) return parameter.segment();

        ChatFormatting formatting = team.getColor();
        if (formatting.isColor()){
            return parameter.segment().withStyle(Style.EMPTY.withColor(formatting));
        }

        return parameter.segment();
    }

    public MutableComponent BOLD(StyleParameter parameter) {
        return parameter.segment().withStyle(Style.EMPTY.withBold(true));
    }

    public MutableComponent ITALIC(StyleParameter parameter) {
        return parameter.segment().withStyle(Style.EMPTY.withItalic(true));
    }

    public MutableComponent UNDERLINE(StyleParameter parameter) {
        return parameter.segment().withStyle(Style.EMPTY.withUnderlined(true));
    }

    public MutableComponent STRIKETHROUGH(StyleParameter parameter) {
        return parameter.segment().withStyle(Style.EMPTY.withStrikethrough(true));
    }

    public MutableComponent OBFUSCATED(StyleParameter parameter) {
        HoverEvent hoverEvent = new HoverEvent.ShowText(Component.literal(parameter.segment().getString()));
        return parameter.segment().withStyle(Style.EMPTY.withObfuscated(true).withHoverEvent(hoverEvent));
    }

    public MutableComponent FONT(StyleParameter parameter) {
        Identifier fontId = Identifier.tryParse(parameter.getString());
        if (fontId == null) return parameter.segment();

        FontDescription font = new FontDescription.Resource(fontId);
        return parameter.segment().withStyle(Style.EMPTY.withFont(font));
    }

    public MutableComponent CLEAR(StyleParameter parameter) {
        return Component.literal(parameter.segment().getString());
    }

    public MutableComponent CLICK_COMMAND_RUN(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.RunCommand(parameter.getString());
        return parameter.segment().withStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableComponent CLICK_COMMAND_SUGGEST(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.SuggestCommand(parameter.getString());
        return parameter.segment().withStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableComponent CLICK_COPY(StyleParameter parameter){
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(parameter.getString());
        return parameter.segment().withStyle(Style.EMPTY.withClickEvent(clickEvent));
    }

    public MutableComponent HOVER_TEXT(StyleParameter parameter){
        HoverEvent hoverEvent = new HoverEvent.ShowText(parameter.getText());
        return parameter.segment().withStyle(Style.EMPTY.withHoverEvent(hoverEvent));
    }

    public MutableComponent HOVER_ITEM(StyleParameter parameter){
        ItemStack item;
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();

        String option = parameter.getString();
        if (option.isBlank()) {
            item = player.getMainHandItem();
        } else {
            int slot = NumberUtils.toInt(option, -1);
            if (slot < 0 || 42 < slot) return parameter.segment();
            item = player.getInventory().getItem(slot);
        }

        if (item.isEmpty()) return parameter.segment();

        HoverEvent hoverEvent = new HoverEvent.ShowItem(ItemStackTemplate.fromNonEmptyStack(item));
        return parameter.segment().withStyle(Style.EMPTY.withHoverEvent(hoverEvent));
    }

    public MutableComponent URL(StyleParameter parameter) {
        URI uri = URI.create(parameter.getString());
        String host = uri.getHost();
        if (host == null){
            return parameter.segment();
        }

        boolean allowed = whitelist.isEmpty() || whitelist.contains(host);
        if (!allowed) {
            for (String domain : whitelist) {
                allowed = host.endsWith("." + domain);
                if (allowed) break;
            }
        }

        if (allowed){
            ClickEvent clickEvent = new ClickEvent.OpenUrl(uri);
            return parameter.segment().withStyle(Style.EMPTY
                    .withClickEvent(clickEvent)
                    .withColor(config.url_color().getRGB()));
        }else {
            return parameter.segment();
        }
    }

    public MutableComponent METADATA(StyleParameter parameter) {
        MutableComponent text = parameter.segment();

        String timestamp = LocalDateTime.now().format(this.timestamp);
        HoverEvent hoverEvent = new HoverEvent.ShowText(Component.literal(timestamp + "\nClick to copy to clipboard").withStyle(ChatFormatting.GRAY));
        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(timestamp + " " + text.getString());

        return text.withStyle(Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
    }

    public MutableComponent UPPER(StyleParameter parameter) {
        String string = parameter.segment().getString();
        return Component.literal(string.toUpperCase()).withStyle(parameter.segment().getStyle());
    }

    public MutableComponent LOWER(StyleParameter parameter) {
        String string = parameter.segment().getString();
        return Component.literal(string.toLowerCase()).withStyle(parameter.segment().getStyle());
    }

    public MutableComponent CAPITALIZE(StyleParameter parameter){
        String string = parameter.segment().getString();
        return Component.literal(StringUtils.capitalize(string)).withStyle(parameter.segment().getStyle());
    }

    public MutableComponent REPLACE(StyleParameter parameter) {
        return parameter.getText().copy().withStyle(parameter.segment().getStyle());
    }

    public MutableComponent MASK(StyleParameter parameter) {
        int length = parameter.segment().getString().length();
        return Component.literal(parameter.getString().repeat(length)).withStyle(parameter.segment().getStyle());
    }

    public MutableComponent PREFIX(StyleParameter parameter){
        MutableComponent prefix = parameter.option().copy();
        return prefix.append(parameter.segment());
    }

    public MutableComponent SUFFIX(StyleParameter parameter){
        MutableComponent suffix = parameter.getText().copy();
        return parameter.segment().append(suffix);
    }

    public MutableComponent SHOW_ITEM(StyleParameter parameter){
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) return parameter.segment();
        Identifier modelId = stack.get(DataComponents.ITEM_MODEL);
        if (modelId == null) return parameter.segment();

        MutableComponent result;
        AtlasSprite atlas = item.get(modelId.toString());

        if ("only_name".equals(parameter.getString())){
            result = stack.getDisplayName().copy();
        } else if (atlas != null) {
            result = Component.object(atlas);
        } else if (stack.getItem() instanceof BlockItem){
            result = stack.getDisplayName().copy();
        } else {
            Identifier atlasId = Identifier.fromNamespaceAndPath(modelId.getNamespace(),"items");
            Identifier spriteId = Identifier.fromNamespaceAndPath(modelId.getNamespace(),String.format("item/%s",modelId.getPath()));;
            result = Component.object(new AtlasSprite(atlasId,spriteId));
        }

        HoverEvent hoverEvent = new HoverEvent.ShowItem(ItemStackTemplate.fromNonEmptyStack(stack));
        ClickEvent clickEvent = new ClickEvent.RunCommand("/embellish-chat open "+player.getUUID());
        InventoryManager.putItem(player,stack);

        return result.withStyle(Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
    }

    public MutableComponent SHOW_INVENTORY(StyleParameter parameter){
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();
        InventoryManager.putInventory(player);

        ClickEvent clickEvent = new ClickEvent.RunCommand("/embellish-chat open "+player.getUUID());
        HoverEvent hoverEvent = new HoverEvent.ShowText(Component.literal(player.getName().getString() + "'s inventory"));
        ResolvableProfile component = ResolvableProfile.createResolved(player.getGameProfile());
        MutableComponent text = Component.object(new PlayerSprite(component, true));

        return text.withStyle(Style.EMPTY.withClickEvent(clickEvent).withHoverEvent(hoverEvent));
    }

    public MutableComponent SHOW_ENDER_CHEST(StyleParameter parameter){
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();
        InventoryManager.putEnderChest(player);

        ClickEvent clickEvent = new ClickEvent.RunCommand("/embellish-chat open "+player.getUUID());
        HoverEvent hoverEvent = new HoverEvent.ShowText(Component.literal(player.getName().getString() + "'s ender chest"));
        ResolvableProfile component = ResolvableProfile.createResolved(player.getGameProfile());
        MutableComponent text = Component.object(new PlayerSprite(component, true));

        return text.withStyle(Style.EMPTY.withClickEvent(clickEvent).withHoverEvent(hoverEvent));
    }

    public MutableComponent ICON_PRESET(StyleParameter parameter) {
        AtlasSprite icon = this.icon.get(parameter.getString());
        if (icon!=null) return Component.object(icon);
        else return parameter.segment();
    }

    public MutableComponent JSON(StyleParameter parameter){
        JsonElement element = JsonParser.parseString(parameter.getString());
        Component text = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE,element).getOrThrow();
        return text.copy();
    }

    public MutableComponent DISCORD_JSON(StyleParameter parameter){
        int index = parameter.getString().indexOf(';');
        String option = parameter.getString();
        if (index != -1) messenger.send(URI.create(option.substring(0,index)),option.substring(index+1));
        return parameter.segment();
    }

    public MutableComponent COMMAND_RUN(StyleParameter parameter){
        ServerPlayer player = parameter.player();
        if (player == null) return parameter.segment();

        MinecraftServer server = player.createCommandSourceStack().getServer();
        CommandSourceStack commandSource = player.createCommandSourceStack();
        server.getCommands().performPrefixedCommand(commandSource, parameter.getString());

        return parameter.segment();
    }

    public MutableComponent LOG(StyleParameter parameter){
        EmbellishChat.LOGGER.info("Log StyleType segment: {}, open segment:{}, sender: {}",parameter.segment().getString(),parameter.getString(),parameter.player());
        return parameter.segment();
    }

    public MutableComponent BUBBLE(StyleParameter parameter){
        if (parameter.player() != null) BubbleUtil.spawnDisplayEntity(parameter.player(),parameter.segment());
        return parameter.segment();
    }

    public MutableComponent BLOCK(StyleParameter parameter) {
        throw new MessageProcessor.MessageBlockedException();
    }
}
