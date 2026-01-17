package io.github.hanhy06.embellishchat.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class OptionUtil {
    public static Text parseOption(String preset, String option, ServerPlayerEntity player){
        String result = preset;
        if (result.isEmpty()) result = option;
        return PlaceHolderUtil.parsedText(result,player);
    }

    public static List<String> split(String option, String  delimiter){
        if (option != null) return List.of(option.split(delimiter));
        return List.of("");
    }
}
