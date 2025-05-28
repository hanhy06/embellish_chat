package com.hanhy06.betterchat.data.model;

import java.util.UUID;

public record MentionData(
        int mention_id,
        UUID receiver,
        UUID sender,
        String timeStamp,
        String message,
        String itemData,
        boolean isOpen) {
}
