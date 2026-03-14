package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.hanhy06.embellishchat.EmbellishChat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

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

        Placeholders.register(Identifier.fromNamespaceAndPath(EmbellishChat.MOD_ID,"content"),(context, string) -> {
            if (context.player() == null) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(context.player().getUUID()));
        });
    }

    public static Component parseTag(String text){
        if (text.isEmpty()) return Component.literal(text);
        return TagParser.DEFAULT.parseText(text,ParserContext.of());
    }

    public static Component parseText(String text, ServerPlayer player){
        if (text.isEmpty()) return Component.literal(text);

        PlaceholderContext context;
        if (player!=null) context = PlaceholderContext.of(player);
        else context = PlaceholderContext.of(EmbellishChat.SERVER);

        return Placeholders.parseText(TagParser.DEFAULT.parseText(text, ParserContext.of()),context);
    }

    public static String parsePlaceholder(String text, ServerPlayer player){
        if (text.isEmpty()) return text;
        else if (player!=null) return Placeholders.parseText(Component.literal(text),PlaceholderContext.of(player)).getString();
        else return Placeholders.parseText(Component.literal(text),PlaceholderContext.of(EmbellishChat.SERVER)).getString();
    }

    public static MutableComponent parsePlaceholder(MutableComponent text, ServerPlayer player){
        if (player!=null) return Placeholders.parseText(text,PlaceholderContext.of(player)).copy();
        else return Placeholders.parseText(text,PlaceholderContext.of(EmbellishChat.SERVER)).copy();
    }
}
