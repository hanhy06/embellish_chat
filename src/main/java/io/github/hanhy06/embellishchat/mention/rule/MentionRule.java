package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Pattern;

public record MentionRule(
        Pattern pattern, String comment,
        MutableText title, Sound sound,
        int cooldown, boolean onlyTarget,
        List<MentionAction> mentions,
        List<StyleAction> styles
) {
    public static MentionRule of(
            String regex, String comment,
            String title, Sound sound,
            int cooldown, boolean onlyTarget,
            List<MentionAction> mentions,
            List<StyleAction> styles
    ){
        return new MentionRule(Pattern.compile(regex), comment, Text.literal(title), sound, cooldown, onlyTarget, mentions, styles);
    }
}
