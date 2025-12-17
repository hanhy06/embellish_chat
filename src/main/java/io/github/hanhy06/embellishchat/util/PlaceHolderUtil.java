package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlaceHolderUtil {
    public static void registerPlaceHolder(){
        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"segment"),(context,argument) -> {
            if (!context.hasPlayer()) return PlaceholderResult.invalid("No player!");

            return PlaceholderResult.value("");
        });

        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"option"),(context,argument) -> {
            if (!context.hasPlayer()) return PlaceholderResult.invalid("No player!");

            return PlaceholderResult.value("");
        });

        Placeholders.register(Identifier.of(EmbellishChat.MOD_ID,"all"),(context,argument) -> {
            if (!context.hasPlayer()) return PlaceholderResult.invalid("No player!");

            return PlaceholderResult.value("");
        });
    }

    public static Text getParsedOption(String option, ServerPlayerEntity player){
        if (player == null) return Text.literal(option);
        return Placeholders.parseText(Text.literal(option), PlaceholderContext.of(player));
    }
}
