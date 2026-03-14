package io.github.hanhy06.embellishchat.styling.data;

import net.minecraft.network.chat.Style;

public record Run(
        int start,
        int end,
        Style style,
        String content
) {}