package com.hanhy06.betterchat.chat.processor;

import com.hanhy06.betterchat.data.model.MentionUnit;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Markdown {
    private final Pattern BOLD = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private final Pattern UNDERLINE = Pattern.compile("(?<!\\\\)__(.+?)__");
    private final Pattern ITALIC = Pattern.compile("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)");
    private final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private final Pattern COLOR = Pattern.compile("(?<!\\\\)(#[0-9A-Fa-f]{6})(.+?)#");

    public Markdown(){

    }

    public MutableText markdown(MutableText context, List<MentionUnit> units){
        if (context == null || context.getString().isBlank()) return context;

        MutableText result = context;

        result = applyStyledPattern(BOLD,result,Style.EMPTY.withBold(true));
        result = applyStyledPattern(UNDERLINE,result,Style.EMPTY.withUnderline(true));
        result = applyStyledPattern(ITALIC,result,Style.EMPTY.withItalic(true));
        result = applyStyledPattern(STRIKETHROUGH,result,Style.EMPTY.withStrikethrough(true));
        result = applyStyledColor(result);
        result = applyStyledMention(result,units);

        return removeEscapeSlashes(result);
    }

    private MutableText applyStyledPattern(Pattern pattern, MutableText context, Style style){
        String str = context.getString();
        Matcher matcher = pattern.matcher(str);

        if (!matcher.find()) return context;

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            result.append(substring(context, lastEnd, matcher.start()));
            result.append(
                    substring(context, matcher.start(1), matcher.end(1)).fillStyle(style)
            );
            lastEnd = matcher.end();
        }

        result.append(substring(context, lastEnd, str.length()));

        return result;
    }

    private MutableText applyStyledColor(MutableText context){
        String str = context.getString();
        Matcher matcher = COLOR.matcher(str);

        if (!matcher.find()) return context;

        MutableText result = Text.empty();
        int lastEnd = 0;

        matcher.reset();
        while (matcher.find()) {
            Color color = Color.decode(matcher.group(1));

            result.append(substring(context, lastEnd, matcher.start()));
            result.append(
                    substring(context, matcher.start(2), matcher.end(2)).fillStyle(Style.EMPTY.withColor(color.getRGB()))
            );
            lastEnd = matcher.end();
        }

        result.append(substring(context, lastEnd, str.length()));

        return result;
    }

    private MutableText applyStyledMention(MutableText context, List<MentionUnit> units){
        if(units == null || units.isEmpty()) return  context;

        MutableText result = Text.empty();
        int lastEnd = 0;

        for (MentionUnit unit: units){
            result.append(substring(context,lastEnd, unit.begin()));
            result.append(substring(context,unit.begin(),unit.end())).fillStyle(
                    Style.EMPTY
                            .withColor(unit.receiver().getTeamColor())
                            .withBold(true)
            );
            lastEnd = unit.end();
        }

        result.append(substring(context, lastEnd, context.getString().length()));

        return result;
    }

    private MutableText removeEscapeSlashes(MutableText context) {
        MutableText result = Text.empty();
        final int[] offset = { 0 };

        context.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                String replaced = content.replaceAll("\\\\([*_~#\\\\])", "$1");
                result.append(Text.literal(replaced).setStyle(style));

                offset[0] += content.length();
                return Optional.empty();
            }
        }, Style.EMPTY);

        return result;
    }


    private MutableText substring(Text text, int beginIndex, int endIndex) {
        if (beginIndex >= endIndex || text == null) {
            return Text.empty();
        }

        MutableText result = Text.empty();
        final int[] currentCharacterOffset = {0};

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                int contentStartOffset = currentCharacterOffset[0];
                int contentEndOffset = contentStartOffset + content.length();

                int effectiveStartIndexInFullText = Math.max(contentStartOffset, beginIndex);
                int effectiveEndIndexInFullText = Math.min(contentEndOffset, endIndex);

                if (effectiveStartIndexInFullText < effectiveEndIndexInFullText) {
                    int subStartIndexInContentPiece = effectiveStartIndexInFullText - contentStartOffset;
                    int subEndIndexInContentPiece = effectiveEndIndexInFullText - contentStartOffset;

                    String subContent = content.substring(subStartIndexInContentPiece, subEndIndexInContentPiece);
                    result.append(Text.literal(subContent).setStyle(style));
                }
                currentCharacterOffset[0] = contentEndOffset;
                return Optional.empty();
            }
        }, Style.EMPTY);

        return result;
    }
}
