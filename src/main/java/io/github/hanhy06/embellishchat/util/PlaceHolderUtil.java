package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaceHolderUtil {
    private static final Map<UUID,String> placeholders = new HashMap<>();
    private static final NodeParser TAG_AND_PLACEHOLDER_PARSER = NodeParser.merge(
            TagParser.DEFAULT,
            Placeholders.SERVER_PLACEHOLDER_PARSER
    );

    public static void put(UUID uuid,String string){
        placeholders.put(uuid,string);
    }

    public static void remove(UUID uuid){
        placeholders.remove(uuid);
    }

    public static void registerPlaceholder(){
        placeholders.clear();

        Placeholders.registerServer(Identifier.fromNamespaceAndPath(EmbellishChat.MOD_ID,"content"),(context, string) -> {
            Player player = context.player();
            if (player == null) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(player.getUUID()));
        });
    }

    public static Component parseTag(String text){
        if (text.isEmpty()) return Component.literal(text);
        return TagParser.DEFAULT.parseComponent(text, ParserContext.of());
    }

    public static Component parseText(String text, ServerPlayer player){
        if (text.isEmpty()) return Component.literal(text);
        return TAG_AND_PLACEHOLDER_PARSER.parseComponent(text, getContext(player).asParserContext());
    }

    public static String parsePlaceholder(String text, ServerPlayer player){
        if (text.isEmpty()) return text;
        return Placeholders.SERVER_PLACEHOLDER_PARSER.parseComponent(text, getContext(player).asParserContext()).getString();
    }

    public static MutableComponent parsePlaceholder(MutableComponent text, ServerPlayer player){
        TextNode node = TextNode.convert(text);
        return Placeholders.SERVER_PLACEHOLDER_PARSER.parseComponent(node, getContext(player).asParserContext()).copy();
    }

    private static ServerPlaceholderContext getContext(ServerPlayer player) {
        if (player != null) {
            return ServerPlaceholderContext.of(player);
        }

        return ServerPlaceholderContext.of(EmbellishChat.SERVER);
    }
}
