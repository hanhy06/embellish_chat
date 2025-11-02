package io.github.hanhy06.embellish_chat.mention.data;

import io.github.hanhy06.embellish_chat.mention.rule.MentionRule;

import java.util.List;
import java.util.Objects;

public record ParsedMention(MentionRule rule, List<String> options, int begin, int end){
    public static ParsedMention of(MentionRule rule, List<String> mentions, int begin, int end){
        return new ParsedMention(rule,mentions,begin,end);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParsedMention mention = (ParsedMention) o;
        return begin == mention.begin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin);
    }
}