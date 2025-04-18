package com.hanhy06.betterchat.config;

import java.util.Collections;
import java.util.List;

public record ConfigData(
        boolean textPreparationEnabled,
        boolean textFilteringEnabled,
        List<String> textFilteringKeywordList,
        boolean mentionEnabled,
        boolean saveMentionEnabled)
{
    public static ConfigData createDefault(){
        return new ConfigData(
                true,
                false,
                Collections.emptyList(),
                true,
                true
        );
    }
}