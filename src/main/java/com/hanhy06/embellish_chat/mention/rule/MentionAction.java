package com.hanhy06.embellish_chat.mention.rule;

public record MentionAction(MentionType mentionType,String preset) {
    public static MentionAction of(MentionType mentionType, String preset){
        return new MentionAction(mentionType,preset);
    }
}
