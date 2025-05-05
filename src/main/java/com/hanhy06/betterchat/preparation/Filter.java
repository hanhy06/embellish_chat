package com.hanhy06.betterchat.preparation;

import java.util.List;

public class Filter {
    private final List<String> words;

    public Filter(List<String> words) {
        this.words = words;
    }

    public String wordBaseFiltering(String str){
        if (words == null || words.isEmpty()) return str;

        String message = str;

        for (String word : words){
            message = message.replace(word,"#".repeat(word.length()));
        }

        return message;
    }
}
