package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.playerdata.PlayerData;

import java.util.UUID;

public record MentionUnit(
        PlayerData receiver,
        int begin,
        int end) {
}
