package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlaceHolderUtil {
    public static Text getParsedOption(String option, ServerPlayerEntity player){
        if (player == null) return Text.literal(option);
        return Placeholders.parseText(Text.literal(option), PlaceholderContext.of(player));
    }
}
