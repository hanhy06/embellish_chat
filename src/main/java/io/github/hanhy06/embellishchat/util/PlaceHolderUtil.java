package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class PlaceHolderUtil {
    private static final Map<ServerPlayerEntity,String> placeholders = new HashMap<>();

    public static void clear(){
        placeholders.clear();
    }

    public static void put(ServerPlayerEntity player,String string){
        placeholders.put(player,string);
    }

    public static void remove(ServerPlayerEntity player){
        placeholders.remove(player);
    }

    public static void register(){
        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"chat"),(context, string) -> {
            if (!context.hasPlayer()) return PlaceholderResult.invalid("no player");
            return PlaceholderResult.value(placeholders.get(context.player()));
        });
    }

    public static Text parsedText(String option, ServerPlayerEntity player){
        if (player == null || option.isEmpty()) return Text.literal(option);
        return Placeholders.parseText(Text.literal(option), PlaceholderContext.of(player));
    }
}
