package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"content"),(context, string) -> {
            if (context.player() == null) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(context.player().getUuid()));
        });
    }

    public static Text parseTag(String text){
        if (text.isEmpty()) return Text.literal(text);
        return TagParser.DEFAULT.parseText(text,ParserContext.of());
    }

    public static Text parseText(String text, ServerPlayerEntity player){
        if (text.isEmpty()) return Text.literal(text);

        PlaceholderContext context;
        if (player!=null) context = PlaceholderContext.of(player);
        else context = PlaceholderContext.of(EmbellishChat.SERVER);

        return Placeholders.parseText(TagParser.DEFAULT.parseText(text, ParserContext.of()),context);
    }

    public static String parsePlaceholder(String text, ServerPlayerEntity player){
        if (text.isEmpty()) return text;
        else if (player!=null) return Placeholders.parseText(Text.literal(text),PlaceholderContext.of(player)).getString();
        else return Placeholders.parseText(Text.literal(text),PlaceholderContext.of(EmbellishChat.SERVER)).getString();
    }

    public static MutableText parsePlaceholder(MutableText text, ServerPlayerEntity player){
        if (player!=null) return Placeholders.parseText(text,PlaceholderContext.of(player)).copy();
        else return Placeholders.parseText(text,PlaceholderContext.of(EmbellishChat.SERVER)).copy();
    }
}
