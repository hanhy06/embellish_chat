package com.hanhy06.embellish_chat.mention.data;

import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import net.minecraft.text.Style;

import java.util.List;

public record Mention(int begin, int end, List<StyleAction> styles, Style style){

}
