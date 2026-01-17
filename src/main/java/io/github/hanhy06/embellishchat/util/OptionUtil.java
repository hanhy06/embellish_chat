package io.github.hanhy06.embellishchat.util;

import java.util.ArrayList;
import java.util.List;

public class OptionUtil {
    public static List<String> selectOption(List<String> options, List<String> presets){
        List<String> result = new ArrayList<>();

        for (int i = 0; i<presets.size();i++){
            String option = presets.get(i);
            if (option.isBlank() && options.size() > i) option = options.get(i);
            result.add(option);
        }

        return result;
    }

    public static List<String> split(String option, String  delimiter){
        if (option != null) return List.of(option.split(delimiter));
        return List.of("");
    }
}
