package io.github.hanhy06.embellishchat.styling.rule;

import net.minecraft.text.MutableText;

import java.util.List;
import java.util.function.Function;

public record StyleNode(int begin, int end,List<String> options, List<Function<StyleParameter, MutableText>> functions,int level) {
}
