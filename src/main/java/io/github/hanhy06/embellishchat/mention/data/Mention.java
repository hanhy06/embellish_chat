package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record Mention(
        int begin,int end,List<String> options,
        HashSet<ServerPlayer> targets,
        Style style,
        MentionRule rule
) {
    public static Mention of(int begin,int end,List<String> options,MentionRule rule){
        return new Mention(begin,end,options,null,null,rule);
    }

    public Mention with(HashSet<ServerPlayer> targets, Style style){
        return new Mention(begin,end,null,targets,style,rule);
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
