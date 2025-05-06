package com.hanhy06.betterchat.preparation;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private static Pattern BOLD = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static Pattern UNDERLINE = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static Pattern ITALIC = Pattern.compile("(?<!\\\\)_(.+)_");
    private static Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static Pattern COLOR = Pattern.compile("(?<!\\\\)(#[0-9A-Fa-f]{6})(.*)");


    public static MutableText markdown(MutableText context){
        if (context == null || context.getString().isEmpty()) return context;

        MutableText result = context;

        result = applyStyledPattern(BOLD,result,Style.EMPTY.withBold(true));
        result = applyStyledPattern(UNDERLINE,result,Style.EMPTY.withUnderline(true));
        result = applyStyledPattern(ITALIC,result,Style.EMPTY.withItalic(true));
        result = applyStyledPattern(STRIKETHROUGH,result,Style.EMPTY.withStrikethrough(true));
        result = applyStyledColor(result);

        return result;
    }

    private static MutableText applyStyledPattern(Pattern pattern, MutableText context, Style style){
        Matcher matcher = pattern.matcher(context.getString());
        if (!matcher.find()) return context;

        MutableText text = Text.empty();
        text.append(substring(context,0,matcher.start()));
        text.append(substring(context,matcher.start(1),matcher.end(1)).fillStyle(style));
        text.append(substring(context,matcher.end(),context.getString().length()));

        return applyStyledPattern(pattern,text,style);
    }

    private static MutableText applyStyledColor(MutableText context){
        Matcher matcher = COLOR.matcher(context.getString());
        if (!matcher.find()) return context;

        Color color = Color.decode(matcher.group(1));

        MutableText text = Text.empty();
        text.append(substring(context,0,matcher.start()));
        text.append(substring(context,matcher.start(2),matcher.end(2)).fillStyle(Style.EMPTY.withColor(color.getRGB())));

        return applyStyledColor(text);
    }

    private static MutableText substring(MutableText context, int beginIndex, int endIndex) {
        if (beginIndex < 0 || beginIndex >= endIndex) {
            return Text.empty();
        }

        MutableText result = Text.empty();

        List<Text> parts = new ArrayList<>();
        parts.add(context);
        parts.addAll(context.getSiblings());

        int currentPos = 0;

        for (Text part : parts) {
            String partString = part.getString();
            int partLen = partString.length();
            int partStart = currentPos;
            int partEnd = currentPos + partLen;

            int overlapStart = Math.max(partStart, beginIndex);
            int overlapEnd = Math.min(partEnd, endIndex);

            if (overlapStart < overlapEnd) {
                int subBeginInPart = overlapStart - partStart;
                int subEndInPart = overlapEnd - partStart;

                String subString = partString.substring(subBeginInPart, subEndInPart);

                MutableText styledSubstring = Text.literal(subString).setStyle(part.getStyle());

                result.append(styledSubstring);
            }

            currentPos = partEnd;

            if (currentPos >= endIndex) {
                break;
            }
        }

        return result;
    }
}
