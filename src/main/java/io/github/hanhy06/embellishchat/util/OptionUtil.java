package io.github.hanhy06.embellishchat.util;

import java.util.List;

public class OptionUtil {
    public static String selectOption(String preset, String option){
        String result = preset;
        if (result.isEmpty()) result = option;
        return result;
    }

    public static List<String> split(String option, String  delimiter){
        if (option != null) return List.of(option.split(delimiter,-1));
        return List.of("");
    }
}
