package com.hanhy06.betterchat.data;

import com.mojang.authlib.GameProfile;

public record Receiver(
        GameProfile profile, int begin, int end
){}
