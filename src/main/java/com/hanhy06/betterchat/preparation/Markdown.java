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
//    private static Pattern ITALIC = Pattern.compile("(?<!\\\\)_(.+)_");
    private static Pattern ITALIC = Pattern.compile("(?<!\\\\)//(.+?)//");
    private static Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static Pattern COLOR = Pattern.compile("(?<!\\\\)#([0-9A-F]{6})(.+?)#");


    public static MutableText markdown(MutableText context){
        if (context == null) return context;

        MutableText result = context;

        List<MarkdownToken> tokens = markdownParser(context.getString());
        for (MarkdownToken token : tokens){
            MutableText temp = Text.empty();

            temp.append(substring(result,0, token.beginIndex));
            temp.append(substring(result,token.beginIndex, token.endIndex).fillStyle(token.style));
            temp.append(substring(result, token.endIndex, result.getString().length()));

            result = temp;
        }

        return result;
    }

    private static List<MarkdownToken> markdownParser(String context){
        List<MarkdownToken> result = new ArrayList<>();

        Matcher bold = BOLD.matcher(context);
        while (bold.find()){
            result.add(new MarkdownToken(
                    Style.EMPTY.withBold(true),
                    bold.start(1),
                    bold.end(1)
            ));
        }

        Matcher underline = UNDERLINE.matcher(context);
        while (underline.find()){
            result.add(new MarkdownToken(
                    Style.EMPTY.withUnderline(true),
                    underline.start(1),
                    underline.end(1)
            ));
        }

        Matcher italic = ITALIC.matcher(context);
        while (italic.find()){
            result.add(new MarkdownToken(
                    Style.EMPTY.withItalic(true),
                    italic.start(1),
                    italic.end(1)
            ));
        }

        Matcher strikethrough = STRIKETHROUGH.matcher(context);
        while (strikethrough.find()){
            result.add(new MarkdownToken(
                    Style.EMPTY.withStrikethrough(true),
                    strikethrough.start(1),
                    strikethrough.end(1)
            ));
        }

        Matcher color = COLOR.matcher(context);
        while (color.find()){
            Color color1 = Color.getColor(color.group(1));

            result.add(new MarkdownToken(
                    Style.EMPTY.withColor(color1.getRGB()),
                    color.start(2),
                    color.end(2)
            ));
        }

        return result;
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

    private record MarkdownToken(
            Style style,
            int beginIndex,
            int endIndex
    ){}
}
