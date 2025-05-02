package com.hanhy06.betterchat.preparation;

import java.util.List;

public class Filter {
    private final List<String> words;

    public Filter(List<String> words) {
        this.words = words;
    }

    public String messageFiltering(String originalMessage){
        if (words == null || words.isEmpty()) return originalMessage;

        String message = originalMessage;

        for (String word : words){
            message = message.replace(word,"#".repeat(word.length()));
        }

        return message;
    }
}
