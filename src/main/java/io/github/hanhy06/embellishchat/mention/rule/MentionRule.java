package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;

import java.util.List;
import java.util.regex.Pattern;

public record MentionRule(
        Pattern pattern,
        String title,
        int cooldown,
        boolean onlyTarget,
        Sound sound,
        List<MentionAction> mentions,
        List<StyleAction> styles
) {
    public static MentionRule of(String regex,String title,int cooldown,boolean onlyTarget,Sound sound,List<MentionAction> mentions, List<StyleAction> styles){
        return new MentionRule(Pattern.compile(regex),title,cooldown,onlyTarget,sound,mentions,styles);
    }
}
