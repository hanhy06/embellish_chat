package com.hanhy06.betterchat.preparation;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Markdown {

    private enum DelimiterType {
        BOLD, UNDERLINE, ITALIC, STRIKETHROUGH, COLOR
    }

    private record DelimiterToken(
            int index,
            int length,
            boolean isOpen,
            DelimiterType type,
            Style style
    ) implements Comparable<DelimiterToken> {
        @Override
        public int compareTo(DelimiterToken other) {
            int indexCompare = Integer.compare(this.index, other.index);
            if (indexCompare != 0) {
                return indexCompare;
            }
            return Boolean.compare(this.isOpen, other.isOpen);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DelimiterToken that = (DelimiterToken) o;
            return index == that.index && length == that.length && isOpen == that.isOpen && type == that.type && Objects.equals(style, that.style);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, length, isOpen, type, style);
        }
    }

    private static final Pattern BOLD_DELIMITER = Pattern.compile("(?<!\\\\)(\\*\\*)");
    private static final Pattern UNDERLINE_DELIMITER = Pattern.compile("(?<!\\\\)(__)");
    private static final Pattern ITALIC_DELIMITER = Pattern.compile("(?<!\\\\)(//)");
    private static final Pattern STRIKETHROUGH_DELIMITER = Pattern.compile("(?<!\\\\)(~~)");
    private static final Pattern COLOR_START_DELIMITER = Pattern.compile("(?<!\\\\)(#([0-9A-Fa-f]{6}))");
    private static final Pattern COLOR_END_DELIMITER = Pattern.compile("(?<!\\\\)(#)(?!([0-9A-Fa-f]{6}))");


    private static List<DelimiterToken> findDelimiters(String text) {
        List<DelimiterToken> tokens = new ArrayList<>();

        Matcher boldMatcher = BOLD_DELIMITER.matcher(text);
        while (boldMatcher.find()) {
            tokens.add(new DelimiterToken(boldMatcher.start(1), 2, true, DelimiterType.BOLD, Style.EMPTY.withBold(true)));
        }

        Matcher underlineMatcher = UNDERLINE_DELIMITER.matcher(text);
        while (underlineMatcher.find()) {
            tokens.add(new DelimiterToken(underlineMatcher.start(1), 2, true, DelimiterType.UNDERLINE, Style.EMPTY.withUnderline(true)));
        }

        Matcher italicMatcher = ITALIC_DELIMITER.matcher(text);
        while (italicMatcher.find()) {
            tokens.add(new DelimiterToken(italicMatcher.start(1), 2, true, DelimiterType.ITALIC, Style.EMPTY.withItalic(true)));
        }

        Matcher strikethroughMatcher = STRIKETHROUGH_DELIMITER.matcher(text);
        while (strikethroughMatcher.find()) {
            tokens.add(new DelimiterToken(strikethroughMatcher.start(1), 2, true, DelimiterType.STRIKETHROUGH, Style.EMPTY.withStrikethrough(true)));
        }

        Matcher colorStartMatcher = COLOR_START_DELIMITER.matcher(text);
        while (colorStartMatcher.find()) {
            try {
                String hex = colorStartMatcher.group(2);
                Color awtColor = Color.decode("#" + hex);
                TextColor mcColor = TextColor.fromRgb(awtColor.getRGB());
                tokens.add(new DelimiterToken(colorStartMatcher.start(1), colorStartMatcher.group(1).length(), true, DelimiterType.COLOR, Style.EMPTY.withColor(mcColor)));
            } catch (NumberFormatException e) {
                System.err.println("Invalid hex color format skipped: " + colorStartMatcher.group(2));
            }
        }

        Matcher colorEndMatcher = COLOR_END_DELIMITER.matcher(text);
        while (colorEndMatcher.find()) {
            tokens.add(new DelimiterToken(colorEndMatcher.start(1), 1, false, DelimiterType.COLOR, null)); // Explicitly a closing token
        }

        Collections.sort(tokens);
        return tokens;
    }

    private static Style calculateCurrentStyle(Stack<DelimiterToken> stack) {
        Style current = Style.EMPTY;
        for (DelimiterToken token : stack) {
            if (token.style != null) {
                current = current.withParent(token.style);
            }
        }
        return current;
    }


    public static MutableText markdown(MutableText context) {
        if (context == null || context.getString().isEmpty()) {
            return context;
        }

        String originalString = context.getString();
        List<DelimiterToken> delimiters = findDelimiters(originalString);

        if (delimiters.isEmpty()) {
            return context;
        }

        MutableText result = Text.empty();
        Stack<DelimiterToken> activeStylesStack = new Stack<>();
        int lastIndex = 0;

        Predicate<DelimiterType> isTypeActive = type ->
                activeStylesStack.stream().anyMatch(t -> t.type == type);

        Supplier<DelimiterToken> findTopmostColorToken = () -> {
            for (int i = activeStylesStack.size() - 1; i >= 0; i--) {
                if (activeStylesStack.get(i).type == DelimiterType.COLOR && activeStylesStack.get(i).isOpen) { // Should always be open on stack
                    return activeStylesStack.get(i);
                }
            }
            return null;
        };


        for (DelimiterToken currentDelimiter : delimiters) {
            if (currentDelimiter.index < lastIndex) {
                continue;
            }

            if (currentDelimiter.index > lastIndex) {
                Style styleToApply = calculateCurrentStyle(activeStylesStack);
                MutableText segment = substring(context, lastIndex, currentDelimiter.index);
                result.append(segment.styled(original -> original.withParent(styleToApply)));
            }

            boolean processed = false;
            if (currentDelimiter.type == DelimiterType.COLOR) {
                if (currentDelimiter.isOpen) { // #RRGGBB
                    activeStylesStack.push(currentDelimiter);
                    processed = true;
                } else { // Closing #
                    DelimiterToken topColor = findTopmostColorToken.get();
                    if (topColor != null) {
                        while (!activeStylesStack.isEmpty()) {
                            DelimiterToken popped = activeStylesStack.pop();
                            if (popped.equals(topColor)) {
                                break;
                            }
                        }
                        processed = true;
                    }
                }
            } else { // Symmetric delimiters: **, __, //, ~~
                boolean currentlyActive = isTypeActive.test(currentDelimiter.type);

                if (currentlyActive && !activeStylesStack.isEmpty() && activeStylesStack.peek().type == currentDelimiter.type) {
                    activeStylesStack.pop(); // Act as closer if type matches stack top
                    processed = true;
                } else if (!currentlyActive) {
                    activeStylesStack.push(currentDelimiter); // Act as opener if type is not active
                    processed = true;
                }
                // If currentlyActive but doesn't match stack top (mismatched nesting), ignore delimiter.
                // If !currentlyActive is false (it is active), but stack top doesn't match -> ignore.
            }

            lastIndex = currentDelimiter.index + currentDelimiter.length;
        }

        if (lastIndex < originalString.length()) {
            Style finalStyle = calculateCurrentStyle(activeStylesStack);
            MutableText remainingSegment = substring(context, lastIndex, originalString.length());
            result.append(remainingSegment.styled(original -> original.withParent(finalStyle)));
        }

        return result;
    }

    private static MutableText substring(MutableText context, int beginIndex, int endIndex) {
        if (context == null || beginIndex < 0 || endIndex <= beginIndex || beginIndex >= context.getString().length()) {
            return Text.empty();
        }
        endIndex = Math.min(endIndex, context.getString().length());


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

                if (subBeginInPart >= 0 && subBeginInPart < subEndInPart && subEndInPart <= partLen) {
                    String subString = partString.substring(subBeginInPart, subEndInPart);
                    if (!subString.isEmpty()) {
                        MutableText styledSubstring = Text.literal(subString).setStyle(part.getStyle());
                        result.append(styledSubstring);
                    }
                }
            }

            currentPos = partEnd;

            if (currentPos >= endIndex) {
                break;
            }
        }

        return result;
    }
}