package com.hanhy06.embellish_chat.styling;

import net.minecraft.text.MutableText;

import java.util.function.BiFunction;

public enum TextStyleApplier {
    COLOR_HEX(TextStyleFunctions::COLOR_HEX),
    COLOR_RAINBOW(TextStyleFunctions::COLOR_RAINBOW),
    COLOR_PRESET(TextStyleFunctions::COLOR_PRESET),
    COLOR_GRADIENT((text,option) -> text),
    COLOR_SHADOW((text,option) -> text),
    FONT(TextStyleFunctions::FONT),
    URL(TextStyleFunctions::URL),
    BOLD(TextStyleFunctions::BOLD),
    ITALIC(TextStyleFunctions::ITALIC),
    UNDERLINE(TextStyleFunctions::UNDERLINE),
    STRIKETHROUGH(TextStyleFunctions::STRIKETHROUGH),
    OBFUSCATED(TextStyleFunctions::OBFUSCATED);

    private final BiFunction<MutableText, String, MutableText> applierFunction;

    TextStyleApplier(BiFunction<MutableText, String, MutableText> applierFunction) {
        this.applierFunction = applierFunction;
    }

    public MutableText apply(MutableText text, String option) {
        return this.applierFunction.apply(text, option);
    }
}
