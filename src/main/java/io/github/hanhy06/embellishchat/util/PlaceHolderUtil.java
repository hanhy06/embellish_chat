package io.github.hanhy06.embellishchat.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlaceHolderUtil {
    public static String getParedOption(String option, ServerPlayerEntity player){
        if (player == null) return option;
        return Placeholders.parseText(Text.literal(option), PlaceholderContext.of(player)).getString();
    }
}
