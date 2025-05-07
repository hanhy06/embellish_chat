package com.hanhy06.betterchat.mention.data;

import java.util.UUID;

public record MentionData(
        UUID sender,
        String timeStamp,
        String originalText,
        String itemData,
        boolean isOpen) {
}
