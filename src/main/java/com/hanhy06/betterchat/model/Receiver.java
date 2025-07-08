package com.hanhy06.betterchat.model;

import com.mojang.authlib.GameProfile;

public record Receiver(
        GameProfile profile, int begin, int end
){}
