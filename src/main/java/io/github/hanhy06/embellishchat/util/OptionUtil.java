package io.github.hanhy06.embellishchat.util;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class OptionUtil {
    public static List<String> parseOption(List<String> options,List<String> presets,ServerPlayerEntity player){
        List<String> result = new ArrayList<>();

        for (int i = 0; i<presets.size();i++){
            String option = presets.get(i);
            if (option.isBlank()) option = options.get(i);
            option = PlaceHolderUtil.parsedText(option,player).getString();
            result.add(option);
        }

        return result;
    }

    public static List<String> split(String option, String  delimiter){
        if (option != null) return List.of(option.split(delimiter));
        return List.of("");
    }
}
