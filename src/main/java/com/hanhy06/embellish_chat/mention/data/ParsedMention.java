package com.hanhy06.embellish_chat.mention.data;

import com.hanhy06.embellish_chat.mention.rule.MentionRule;

import java.util.Objects;

public record ParsedMention(MentionRule rule, String mention, int begin, int end){
    public static ParsedMention of(MentionRule rule, String mention, int begin, int end){
        return new ParsedMention(rule,mention,begin,end);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParsedMention mention = (ParsedMention) o;
        return end == mention.end && begin == mention.begin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }
}