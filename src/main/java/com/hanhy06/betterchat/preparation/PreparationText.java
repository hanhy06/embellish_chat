package com.hanhy06.betterchat.preparation;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PreparationText {
    private static class ParsingContext {
        final String content;
        final PlayerManager manager;
        final MutableText output = Text.literal("");
        final StringBuilder buffer = new StringBuilder();
        Style currentStyle = Style.EMPTY;
        int index = 0;

        ParsingContext(String content, PlayerManager manager) {
            this.content = content;
            this.manager = manager;
        }

        char currentChar() {
            return content.charAt(index);
        }

        boolean hasNext(int offset) {
            return index + offset < content.length();
        }

        char charAt(int offset) {
            return content.charAt(index + offset);
        }

        void advance(int steps) {
            index += steps;
        }

        void appendToBuffer(char c) {
            buffer.append(c);
        }

        void flushBuffer() {
            if (buffer.length() > 0) {
                output.append(Text.literal(buffer.toString()).setStyle(currentStyle));
                buffer.setLength(0);
            }
        }
    }

    public static Text prepText(String content, PlayerManager manager) {
        ParsingContext context = new ParsingContext(content, manager);

        while (context.index < context.content.length()) {
            char c = context.currentChar();

            if (handleEscape(context)) continue;
            if (handleMarkdown(context)) continue;
            if (handleMention(context)) continue;
            if (handleColorCode(context)) continue;

            context.appendToBuffer(c);
            context.advance(1);
        }

        context.flushBuffer();
        return addMataData(context.output);
    }

    private static boolean handleEscape(ParsingContext context) {
        if (context.currentChar() == '\\' && context.hasNext(1)) {
            context.flushBuffer();
            context.appendToBuffer(context.charAt(1));
            context.advance(2);
            return true;
        }
        return false;
    }

    private static boolean handleMarkdown(ParsingContext context) {
        char c = context.currentChar();
        char next = context.hasNext(1) ? context.charAt(1) : '\0';

        Style toggledStyle = null;
        int advanceSteps = 0;

        if (c == '*' && next == '*') {
            toggledStyle = context.currentStyle.withBold(!context.currentStyle.isBold());
            advanceSteps = 2;
        } else if (c == '_' && next == '_') {
            toggledStyle = context.currentStyle.withUnderline(!context.currentStyle.isUnderlined());
            advanceSteps = 2;
        } else if (c == '~' && next == '~') {
            toggledStyle = context.currentStyle.withStrikethrough(!context.currentStyle.isStrikethrough());
            advanceSteps = 2;
        } else if (c == '_') {
            toggledStyle = context.currentStyle.withItalic(!context.currentStyle.isItalic());
            advanceSteps = 1;
        }

        if (toggledStyle != null) {
            context.flushBuffer();
            context.currentStyle = toggledStyle;
            context.advance(advanceSteps);
            return true;
        }
        return false;
    }

    private static boolean handleMention(ParsingContext context) {
        if (context.currentChar() != '@' || !context.hasNext(1)) {
            return false;
        }

        String matchedName = findLongestMatchingPlayerName(context.content, context.index + 1, context.manager);

        if (matchedName != null) {
            context.flushBuffer();

            ServerPlayerEntity player = context.manager.getPlayer(matchedName);
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP), SoundCategory.MASTER,player.getX(),player.getY(),player.getZ(),1f,1.75f,1));
            TextColor mentionColor = getPlayerMentionColor(player);
            Style mentionStyle = context.currentStyle.withColor(mentionColor).withBold(true).withShadowColor(mentionColor.getRgb());

            context.output.append(Text.literal("@" + matchedName).setStyle(mentionStyle));
            context.advance(1 + matchedName.length());
            return true;
        }
        return false;
    }


    private static boolean handleColorCode(ParsingContext context) {
        if (context.currentChar() != '[' || !context.hasNext(1) || context.charAt(1) != '#') {
            return false;
        }

        int closingBracketIndex = context.content.indexOf(']', context.index + 2);
        if (closingBracketIndex == -1) {
            return false;
        }

        String inside = context.content.substring(context.index + 2, closingBracketIndex);
        Optional<TextColor> newColorOpt = extractColor(inside);
        String textToColor = extractText(inside);

        if (newColorOpt.isPresent() && !textToColor.isEmpty()) {
            context.flushBuffer();
            context.output.append(
                    Text.literal(textToColor)
                            .setStyle(context.currentStyle.withColor(newColorOpt.get()))
            );

            context.advance(closingBracketIndex - context.index + 1);
            return true;
        }

        return false;
    }

    private static String findLongestMatchingPlayerName(String content, int startIndex, PlayerManager manager) {
        String longestMatch = null;
        for (String name : manager.getPlayerNames()) {

            if (content.startsWith(name, startIndex)) {
                int nameEndIndex = startIndex + name.length();
                if (nameEndIndex == content.length() || !Character.isLetterOrDigit(content.charAt(nameEndIndex))) {
                    if (longestMatch == null || name.length() > longestMatch.length()) {
                        longestMatch = name;
                    }
                }
            }
        }
        return longestMatch;
    }

    private static TextColor getPlayerMentionColor(ServerPlayerEntity player) {
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

    private static Optional<TextColor> extractColor(String inside) {
        StringBuilder hexBuilder = new StringBuilder();
        int i = 0;
        while (i < inside.length() && isHexChar(inside.charAt(i)) && hexBuilder.length() < 8) {
            hexBuilder.append(inside.charAt(i));
            i++;
        }

        String hex = hexBuilder.toString();
        if (hex.length() != 6 && hex.length() != 8) {
            return Optional.empty();
        }

        try {
            if (hex.length() == 8) {
                int rgb = Integer.parseInt(hex.substring(2), 16);
                return Optional.of(TextColor.fromRgb(rgb));
            } else {
                int rgb = Integer.parseInt(hex, 16);
                return Optional.of(TextColor.fromRgb(rgb));
            }
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    private static String extractText(String inside) {
        int i = 0;
        while (i < inside.length() && isHexChar(inside.charAt(i)) && i < 8) {
            i++;
        }

        if (i != 6 && i != 8) {
            int hexPrefixLength = 0;
            while(hexPrefixLength < inside.length() && isHexChar(inside.charAt(hexPrefixLength)) && hexPrefixLength < 8) {
                hexPrefixLength++;
            }
            if (hexPrefixLength != 6 && hexPrefixLength != 8) return inside;
            i = hexPrefixLength;
        }

        if (i >= inside.length()) {
            return "";
        }
        return inside.substring(i);
    }

    private static boolean isHexChar(char c) {
        return (c >= '0' && c <= '9')
                || (c >= 'A' && c <= 'F')
                || (c >= 'a' && c <= 'f');
    }

    private static MutableText addMataData(MutableText input){
        MutableText output;

        Instant now = Instant.now();
        ZoneId id = ZoneId.systemDefault();
        ZonedDateTime time = now.atZone(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초에 전송됨.\n클릭시 클립보드에 복사.");
        HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(
                time.format(formatter)
        ));

        ClickEvent clickEvent = new ClickEvent.CopyToClipboard(input.getString());

        Style style = input.getStyle();
        style = style
                .withHoverEvent(hoverEvent)
                .withClickEvent(clickEvent);

        output = input.copy().fillStyle(style);
        return output;
    }
}