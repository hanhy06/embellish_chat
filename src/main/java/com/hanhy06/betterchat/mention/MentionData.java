package com.hanhy06.betterchat.mention;

import java.util.UUID;

public record MentionData(
        UUID sender,
        String timeStamp,
        String originalText,
        String itemData,
        boolean isOpen) {
}
