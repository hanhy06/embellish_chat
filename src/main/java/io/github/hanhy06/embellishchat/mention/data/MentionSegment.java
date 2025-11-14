package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import net.minecraft.text.Style;

import java.util.List;

public record MentionSegment(int begin, int end, List<StyleAction> styles, Style style){

}
