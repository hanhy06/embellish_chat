package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.styling.rule.StyleAction;

import java.util.List;
import java.util.regex.Pattern;

public record MentionRule(Pattern pattern, MentionType mentionType, List<StyleAction> actions) {
    public static MentionRule of(String regex, MentionType mentionType, List<StyleAction> actions){
        return new MentionRule(Pattern.compile(regex),mentionType,actions);
    }
}
