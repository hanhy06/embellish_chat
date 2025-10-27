package com.hanhy06.embellish_chat.mention.data;

public record ParsedMention(String mention, int begin, int end){
    public static ParsedMention of(String mention, int begin, int end){
        return new ParsedMention(mention,begin,end);
    }
}