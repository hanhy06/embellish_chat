package com.hanhy06.fancy_chat.chat.processor;

import com.hanhy06.fancy_chat.FancyChat;
import com.hanhy06.fancy_chat.data.Receiver;
import com.hanhy06.fancy_chat.util.Metadata;
import com.hanhy06.fancy_chat.util.Teamcolor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyledTextProcessor {
    private static final Pattern BOLD = Pattern.compile("(?<!\\\\)\\*\\*(.+?)\\*\\*");
    private static final Pattern UNDERLINE = Pattern.compile("(?<!\\\\)__(.+?)__");
    private static final Pattern ITALIC = Pattern.compile("(?<!\\\\)(?<!_)_([^_]+?)_(?!_)");
    private static final Pattern STRIKETHROUGH = Pattern.compile("(?<!\\\\)~~(.+?)~~");
    private static final Pattern OBFUSCATED = Pattern.compile("(?<!\\\\)\\|\\|(.+?)\\|\\|");
    private static final Pattern COLOR = Pattern.compile("(?<!\\\\)(#[0-9A-Fa-f]{6})(.+?)#");

    public static MutableText applyStyles(MutableText context, List<Receiver> receivers){
        if (context == null || context.getString().isBlank()) return context;

        MutableText result = context;

        result = applyStyledMention(result,receivers);
        result = applyStyledColor(result);
        result = applyStyledPattern(BOLD,result,Style.EMPTY.withBold(true));
        result = applyStyledPattern(UNDERLINE,result,Style.EMPTY.withUnderline(true));
        result = applyStyledPattern(ITALIC,result,Style.EMPTY.withItalic(true));
        result = applyStyledPattern(STRIKETHROUGH,result,Style.EMPTY.withStrikethrough(true));
        result = applyStyledPattern(OBFUSCATED,result,Style.EMPTY.withObfuscated(true));
        result = removeEscapeSlashes(result);

        return Metadata.metadata(result);
    }

    private static MutableText applyStyledPattern(Pattern pattern, MutableText context, Style style){
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

    private static MutableText applyStyledColor(MutableText context){
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
                    substring(context, matcher.start(2), matcher.end(2))
                            .fillStyle(
                                    Style.EMPTY.withColor(color.getRGB()
                                    )
                    )
            );
            lastEnd = matcher.end();
        }

        result.append(substring(context, lastEnd, str.length()));

        return result;
    }

    private static MutableText applyStyledMention(MutableText context, List<Receiver> receivers){
        if(receivers == null || receivers.isEmpty()) return  context;

        MutableText result = Text.empty();
        int lastEnd = 0;

        for (Receiver receiver: receivers){
            ServerPlayerEntity player = FancyChat.getPlayerManager().getPlayer(receiver.profile().getId());

            result.append(substring(context,lastEnd, receiver.begin()));
            result.append(
                    substring(context, receiver.begin(), receiver.end())
                            .fillStyle(
                                    Style.EMPTY.withColor(Teamcolor.getPlayerColor(player)).withBold(true)
                            )
            );
            lastEnd = receiver.end();
        }

        result.append(substring(context, lastEnd, context.getString().length()));

        return result;
    }

    private static MutableText removeEscapeSlashes(MutableText context) {
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


    private static MutableText substring(Text text, int beginIndex, int endIndex) {
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
