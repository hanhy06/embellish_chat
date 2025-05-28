package com.hanhy06.betterchat.data.model;

public record MentionUnit(
        PlayerData receiver,
        int begin,
        int end) {
}
