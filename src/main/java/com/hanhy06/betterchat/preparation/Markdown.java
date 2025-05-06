package com.hanhy06.betterchat.preparation;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Markdown {

    private static final Pattern BOLD          = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static final Pattern UNDERLINE     = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static final Pattern ITALIC        = Pattern.compile("(?<!\\\\)_(.+?)_");
    private static final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static final Pattern COLOR         = Pattern.compile("(?<!\\\\)#([0-9A-Fa-f]{6})(.+)", Pattern.DOTALL);

    private Markdown() {}

    public static MutableText markdown(MutableText original) {
        if (original == null || original.getString().isEmpty()) return original;

        MutableText out = original.copy();
        out = applyInline(BOLD,          out, Style.EMPTY.withBold(true));
        out = applyInline(UNDERLINE,     out, Style.EMPTY.withUnderline(true));
        out = applyInline(ITALIC,        out, Style.EMPTY.withItalic(true));
        out = applyInline(STRIKETHROUGH, out, Style.EMPTY.withStrikethrough(true));
        out = applyColor(out);
        return out;
    }

    private static MutableText applyInline(Pattern p, MutableText ctx, Style style) {
        Matcher m = p.matcher(ctx.getString());
        if (!m.find()) return ctx;

        MutableText rebuilt = Text.empty();
        rebuilt.append(substring(ctx, 0, m.start()));
        rebuilt.append(substring(ctx, m.start(1), m.end(1)).fillStyle(style));
        rebuilt.append(substring(ctx, m.end(), ctx.getString().length()));
        return applyInline(p, rebuilt, style);
    }

    private static MutableText applyColor(MutableText ctx) {
        Matcher m = COLOR.matcher(ctx.getString());
        if (!m.find()) return ctx;

        int rgb = Integer.parseInt(m.group(1), 16) & 0xFFFFFF;
        Style color = Style.EMPTY.withColor(TextColor.fromRgb(rgb));

        MutableText rebuilt = Text.empty();
        rebuilt.append(substring(ctx, 0, m.start()));
        rebuilt.append(substring(ctx, m.start(2), m.end(2)).fillStyle(color));
        return applyColor(rebuilt);
    }

    private static MutableText substring(Text src, int begin, int end) {
        if (begin >= end) return Text.empty();

        MutableText result = Text.empty();
        Deque<Text> queue = new ArrayDeque<>();
        queue.add(src);

        int cursor = 0;

        while (!queue.isEmpty()) {
            Text node = queue.removeFirst();
            String own = node.copy().getString();
            List<Text> children = new ArrayList<>(node.getSiblings());

            int ownLen = own.length();
            int nodeStart = cursor;
            int nodeEnd   = cursor + ownLen;

            int clipStart = Math.max(nodeStart, begin);
            int clipEnd   = Math.min(nodeEnd,   end);

            if (clipStart < clipEnd) {
                int s = clipStart - nodeStart;
                int e = clipEnd   - nodeStart;
                String frag = own.substring(s, e);
                result.append(Text.literal(frag).setStyle(node.getStyle()));
            }

            cursor = nodeEnd;
            queue.addAll(children);

            if (cursor >= end) break;
        }
        return result;
    }
}
