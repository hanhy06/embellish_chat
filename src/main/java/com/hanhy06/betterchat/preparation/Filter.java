package com.hanhy06.betterchat.preparation;

import java.util.List;

public class Filter {
    private final List<String> words;

    public Filter(List<String> words) {
        this.words = words;
    }

    public String wordBaseFiltering(String str){
        if (words == null || words.isEmpty() || str == null || str.isBlank()) return str;

        String result = str;

        for (String word : words){
            result = result.replace(word,"#".repeat(word.length()));
        }

        return result;
    }
}
