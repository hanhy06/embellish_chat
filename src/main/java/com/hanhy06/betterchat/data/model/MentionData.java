package com.hanhy06.betterchat.data.model;

import java.util.UUID;

public record MentionData(
        UUID sender,
        UUID receiver,
        String timeStamp,
        String originalText,
        String itemData,
        boolean isOpen) {
}
