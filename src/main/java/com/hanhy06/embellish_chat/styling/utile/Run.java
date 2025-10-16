package com.hanhy06.embellish_chat.styling.utile;

import net.minecraft.text.Style;

public record Run(
        int start,
        int end,
        Style style,
        String content
) {}