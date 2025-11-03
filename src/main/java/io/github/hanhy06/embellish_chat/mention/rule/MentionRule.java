package io.github.hanhy06.embellish_chat.mention.rule;

import io.github.hanhy06.embellish_chat.styling.rule.StyleAction;

import java.util.List;
import java.util.regex.Pattern;

public record MentionRule(Pattern pattern, List<MentionAction> mentions, List<StyleAction> styles) {
    public static MentionRule of(String regex, List<MentionAction> mentions, List<StyleAction> styles){
        return new MentionRule(Pattern.compile(regex),mentions,styles);
    }
}
