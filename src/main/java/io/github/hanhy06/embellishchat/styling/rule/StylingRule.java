package io.github.hanhy06.embellishchat.styling.rule;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Pattern;

public record StylingRule(Pattern pattern, MutableText comment, List<StyleAction> styles) {
    public static StylingRule of(String regex, String comment, List<StyleAction> styles){
        return new StylingRule(Pattern.compile(regex), Text.literal(comment),styles);
    }
}
