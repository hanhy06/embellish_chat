package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record Mention(
        int begin,int end,List<String> options,
        HashSet<ServerPlayerEntity> targets,
         Style style,
        MentionRule rule
) {
    public static Mention of(int begin,int end,List<String> options,MentionRule rule){
        return new Mention(begin,end,options,new HashSet<>(),Style.EMPTY,rule);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mention mention = (Mention) o;
        return begin == mention.begin;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(begin);
    }
}
