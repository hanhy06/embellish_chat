package io.github.hanhy06.embellishchat.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class OptionUtil {
    public static List<String> parseOption(List<String> options,List<String> presets,ServerPlayerEntity player){
        List<String> result = new ArrayList<>();

        for (int i = 0; i<presets.size();i++){
            String option = parseOption(options.get(i),presets.get(i),player);
            result.add(option);
        }

        return result;
    }

    public static String parseOption(String option, String preset, ServerPlayerEntity player){
        String value = preset.isBlank() ? option : preset;

        if (!FabricLoader.getInstance().isModLoaded("placeholder-api")){
            value = PlaceHolderUtil.getParedOption(option,player);
        }

        return value;
    }
}
