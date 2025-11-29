package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.regex.Pattern;

public record MentionRule(
        Pattern pattern,
        Identifier sound,
        float pitch,
        String title,
        int cooldown,
        List<MentionAction> mentions,
        List<StyleAction> styles
) {
    public static MentionRule of(String regex, Identifier sound, float pitch, String title,int cooldown, List<MentionAction> mentions, List<StyleAction> styles){
        return new MentionRule(Pattern.compile(regex),sound,pitch,title,cooldown,mentions,styles);
    }
}
