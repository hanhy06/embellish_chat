package io.github.hanhy06.embellishchat.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class OptionUtil {
    public static String selectOption(String preset, String option, ServerPlayerEntity player){
        String result = preset;
        if (result.isEmpty()) result = option;
        return result;
    }

    public static List<String> split(String option, String  delimiter){
        if (option != null) return List.of(option.split(delimiter));
        return List.of("");
    }
}
