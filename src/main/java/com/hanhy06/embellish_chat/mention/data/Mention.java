package com.hanhy06.embellish_chat.mention.data;

import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import net.minecraft.text.Style;

import java.util.List;
import java.util.Objects;

public record Mention(int begin, int end, List<StyleAction> styles, Style style){
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mention mention = (Mention) o;
        return end == mention.end && begin == mention.begin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }
}
