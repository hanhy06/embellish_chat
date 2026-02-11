package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PlaceHolderUtil {
    private static final Map<ServerPlayerEntity,String> placeholders = new HashMap<>();

    public static void put(ServerPlayerEntity player,String string){
        placeholders.put(player,string);
    }

    public static void remove(ServerPlayerEntity player){
        placeholders.remove(player);
    }

    public static void registerPlaceholder(){
        placeholders.clear();

        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"content"),(context, string) -> {
            if (!context.hasPlayer()) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(context.player()));
        });
    }

    public static Text parseTag(String text){
        if (text.isEmpty()) return Text.literal(text);
        return TagParser.DEFAULT.parseText(text,ParserContext.of());
    }

    public static Text parseText(String option, ServerPlayerEntity player){
        if (option.isEmpty()) return Text.literal(option);

        PlaceholderContext context;
        if (player!=null) context = PlaceholderContext.of(player);
        else context = PlaceholderContext.of(EmbellishChat.SERVER);

        return Placeholders.parseText(TagParser.DEFAULT.parseText(option, ParserContext.of()),context);
    }

    public static String parsePlaceholder(String option, ServerPlayerEntity player){
        if (option.isEmpty()) return option;
        else if (player!=null) return Placeholders.parseText(Text.literal(option),PlaceholderContext.of(player)).getString();
        else return Placeholders.parseText(Text.literal(option),PlaceholderContext.of(EmbellishChat.SERVER)).getString();
    }
}
