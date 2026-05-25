package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaceHolderUtil {
    private static final Map<UUID,String> placeholders = new HashMap<>();

    public static void put(UUID uuid,String string){
        placeholders.put(uuid,string);
    }

    public static void remove(UUID uuid){
        placeholders.remove(uuid);
    }

    public static void registerPlaceholder(){
        placeholders.clear();

        Placeholders.register(ResourceLocation.fromNamespaceAndPath(EmbellishChat.MOD_ID,"content"),(context, string) -> {
            Player player = context.player();
            if (player == null) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(player.getUUID()));
        });
    }

    public static Component parseTag(String text){
        if (text.isEmpty()) return Component.literal(text);
        return TextNode.asSingle(TagParser.DEFAULT.parseNodes(TextNode.of(text))).toText(ParserContext.of());
    }

    public static Component parseText(String text, ServerPlayer player){
        if (text.isEmpty()) return Component.literal(text);

        ParserContext context = getParserContext(player);

        TextNode placeholderText = Placeholders.parseNodes(TextNode.of(text));
        TextNode taggedText = TextNode.asSingle(TagParser.DEFAULT.parseNodes(placeholderText));

        return taggedText.toText(context);
    }

    public static String parsePlaceholder(String text, ServerPlayer player){
        if (text.isEmpty()) return text;

        ParserContext context = getParserContext(player);

        TextNode placeholderText = Placeholders.parseNodes(TextNode.of(text));

        return placeholderText.toText(context).getString();
    }

    public static MutableComponent parsePlaceholder(MutableComponent text, ServerPlayer player){
        ParserContext context = getParserContext(player);

        TextNode placeholderText = Placeholders.parseNodes(TextNode.convert(text));

        return placeholderText.toText(context).copy();
    }

    private static ParserContext getParserContext(ServerPlayer player) {
        if (player != null) return PlaceholderContext.of(player).asParserContext();
        return PlaceholderContext.of(EmbellishChat.SERVER).asParserContext();
    }
}
