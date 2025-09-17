package com.hanhy06.fancy_chat.data;

import com.mojang.authlib.GameProfile;

public record Receiver(
        GameProfile profile, int begin, int end
){}
