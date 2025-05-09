package com.hanhy06.betterchat.preparation;

import com.hanhy06.betterchat.mention.Mention;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

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
    private final Pattern COLOR = Pattern.compile("(?<!\\\\)(#[0-9A-Fa-f]{6})(.*)");

    private final PlayerManager manager;

    public Markdown(PlayerManager manager){
        this.manager = manager;
    }

    public MutableText markdown(MutableText context, List<Mention.MentionUnit> tokens){
        if (context == null || context.getString().isBlank()) return context;

        MutableText result = context;

        result = applyStyledPattern(BOLD,result,Style.EMPTY.withBold(true));
        result = applyStyledPattern(UNDERLINE,result,Style.EMPTY.withUnderline(true));
        result = applyStyledPattern(ITALIC,result,Style.EMPTY.withItalic(true));
        result = applyStyledPattern(STRIKETHROUGH,result,Style.EMPTY.withStrikethrough(true));
        result = applyStyledColor(result);
        result = applyStyledMention(result,tokens);

        return result;
    }

    private MutableText applyStyledPattern(Pattern pattern, MutableText context, Style style){
        Matcher matcher = pattern.matcher(context.getString());
        if (!matcher.find()) return context;

        MutableText text = Text.empty();
        text.append(substring(context,0,matcher.start()));
        text.append(substring(context,matcher.start(1),matcher.end(1)).fillStyle(style));
        text.append(substring(context,matcher.end(),context.getString().length()));

        return applyStyledPattern(pattern,text,style);
    }

    private MutableText applyStyledColor(MutableText context){
        Matcher matcher = COLOR.matcher(context.getString());
        if (!matcher.find()) return context;

        Color color = Color.decode(matcher.group(1));

        MutableText text = Text.empty();
        text.append(substring(context,0,matcher.start()));
        text.append(substring(context,matcher.start(2),matcher.end(2)).fillStyle(Style.EMPTY.withColor(color.getRGB())));

        return applyStyledColor(text);
    }

    private MutableText applyStyledMention(MutableText context, List<Mention.MentionUnit> tokens){
        MutableText result = context;

        for (Mention.MentionUnit token : tokens){
            MutableText temp = Text.empty();

            temp.append(substring(result,0,token.begin()));
            temp.append(substring(result,token.begin(), token.end()).fillStyle(Style.EMPTY
                    .withColor(getPlayerColor(token.name()))
                    .withBold(true)
            ));
            temp.append(substring(result,token.end(),result.getString().length()));

            result = temp;
        }

        return result;
    }

    private TextColor getPlayerColor(String name) {
        ServerPlayerEntity player = manager.getPlayer(name);

        if (player != null) {
            Team team = player.getScoreboardTeam();
            if (team != null) {
                Formatting formatting = team.getColor();
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                    return TextColor.fromFormatting(formatting);
                }
            }
        }

        return TextColor.fromFormatting(Formatting.YELLOW);
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
