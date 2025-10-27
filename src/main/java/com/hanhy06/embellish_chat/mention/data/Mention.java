package com.hanhy06.embellish_chat.mention.data;

import net.minecraft.text.MutableText;

public record Mention(
        int begin, int end, MutableText text
){
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Mention other)) return false;
        return this.begin == other.begin && this.end == other.end;
    }

    @Override
    public int hashCode() {
        return 31 * begin + end;
    }
}
