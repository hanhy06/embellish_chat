package com.hanhy06.betterchat.mixin.client;

import com.hanhy06.betterchat.data.CursorPosition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    // Shadows
    @Shadow protected TextFieldWidget chatField;
    @Shadow ChatInputSuggestor chatInputSuggestor;
    @Shadow public void sendMessage(String chatText, boolean addToHistory) {}
    @Shadow public void setChatFromHistory(int offset) {}

    // UI constants
    @Unique private static final int LINE_HEIGHT = 9;
    @Unique private static final int LEFT_PADDING = 4;
    @Unique private static final int BOX_MARGIN = 2;
    @Unique private static final int CURSOR_WIDTH = 1;

    // State
    @Unique private final Set<Integer> lineBreakPositions = new TreeSet<>(); // 정렬/중복제거
    @Unique private int preferredColumnIndex = -1;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    // ========== Input ==========
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            resetPreferredColumn();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;

        // ESC
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }

        // ENTER & SHIFT+ENTER
        if (isEnter(keyCode)) {
            if (hasShiftDown()) {
                addLineBreakAtCursor();
                resetPreferredColumn();
                return true;
            }
            sendMessage(this.chatField.getText(), true);
            this.client.setScreen(null);
            return true;
        }

        // History with Shift
        if (keyCode == GLFW.GLFW_KEY_UP && hasShiftDown()) {
            resetPreferredColumn();
            setChatFromHistory(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN && hasShiftDown()) {
            resetPreferredColumn();
            setChatFromHistory(1);
            return true;
        }

        // Vertical move within virtual lines
        if (keyCode == GLFW.GLFW_KEY_UP) {
            moveCursorVertically(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            moveCursorVertically(+1);
            return true;
        }

        // Page scroll
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            var chatHud = this.client.inGameHud.getChatHud();
            chatHud.scroll(chatHud.getVisibleLineCount() - 1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            var chatHud = this.client.inGameHud.getChatHud();
            chatHud.scroll(-chatHud.getVisibleLineCount() + 1);
            return true;
        }

        // Fallback to text field
        boolean handled = this.chatField.keyPressed(keyCode, scanCode, modifiers);
        if (handled) resetPreferredColumn();
        return handled;
    }

    // ========== Render ==========
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        var chatHud = this.client.inGameHud.getChatHud();
        chatHud.render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);

        List<String> lines = splitToLines(this.chatField.getText(), lineBreakPositions);
        int lineCount = lines.size();
        int topY = computeTopY(lineCount);

        // Input box background
        context.fill(
                BOX_MARGIN, topY - 2,
                this.width - BOX_MARGIN, this.height - BOX_MARGIN,
                this.client.options.getTextBackgroundColor(Integer.MIN_VALUE)
        );

        // Text
        for (int i = 0; i < lineCount; i++) {
            context.drawTextWithShadow(this.textRenderer, lines.get(i), LEFT_PADDING, topY + i * LINE_HEIGHT, 0xFFFFFFFF);
        }

        // Caret
        if (shouldBlink()) {
            CursorPosition caret = convertCursorTo2D(lines, this.chatField.getCursor());
            String currentLine = lines.get(caret.line());
            int caretX = LEFT_PADDING + this.textRenderer.getWidth(currentLine.substring(0, caret.column()));
            int caretY = topY + caret.line() * LINE_HEIGHT;
            context.fill(caretX, caretY - 1, caretX + CURSOR_WIDTH, caretY + LINE_HEIGHT, 0xFFE0E0E0);
        }

        // Suggestor & hovers
        this.chatInputSuggestor.render(context, mouseX, mouseY);

        MessageIndicator indicator = chatHud.getIndicatorAt(mouseX, mouseY);
        if (indicator != null && indicator.text() != null) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(indicator.text(), 210), mouseX, mouseY);
        } else {
            Style style = chatHud.getTextStyleAt(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null) {
                context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
            }
        }
    }

    // ========== Helpers ==========
    @Unique
    private void moveCursorVertically(int deltaLine) {
        List<String> lines = splitToLines(chatField.getText(), lineBreakPositions);
        CursorPosition pos = convertCursorTo2D(lines, chatField.getCursor());

        if (preferredColumnIndex == -1) preferredColumnIndex = pos.column();

        int targetLine = clamp(pos.line() + deltaLine, 0, lines.size() - 1);
        int targetColumn = Math.min(preferredColumnIndex, lines.get(targetLine).length());

        chatField.setCursor(convertCursorTo1D(lines, targetLine, targetColumn), false);
    }

    @Unique
    private void addLineBreakAtCursor() {
        int cursor = chatField.getCursor();
        lineBreakPositions.add(cursor);
    }

    @Unique
    private void resetPreferredColumn() {
        preferredColumnIndex = -1;
    }

    @Unique
    private boolean isEnter(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER;
    }

    @Unique
    private int computeTopY(int lineCount) {
        return this.height - 14 - (lineCount - 1) * LINE_HEIGHT;
    }

    @Unique
    private boolean shouldBlink() {
        return this.client.inGameHud.getTicks() / 6 % 2 == 0;
    }

    // ========== Text <-> lines ==========
    @Unique
    private List<String> splitToLines(String text, Set<Integer> breakPositions) {
        List<String> lines = new ArrayList<>();
        int last = 0;
        for (int pos : breakPositions) {
            if (pos > text.length()) continue;
            if (pos > last) {
                lines.add(text.substring(last, pos));
                last = pos;
            }
        }
        lines.add(text.substring(last));
        return lines;
    }

    @Unique
    private CursorPosition convertCursorTo2D(List<String> lines, int cursorIndex) {
        int consumed = 0;
        for (int i = 0; i < lines.size(); i++) {
            int len = lines.get(i).length();
            if (cursorIndex <= consumed + len) {
                return new CursorPosition(i, cursorIndex - consumed);
            }
            consumed += len;
        }
        int lastLine = lines.size() - 1;
        return new CursorPosition(lastLine, lines.get(lastLine).length());
    }

    @Unique
    private int convertCursorTo1D(List<String> lines, int line, int column) {
        int index = 0;
        for (int i = 0; i < line; i++) index += lines.get(i).length();
        return index + column;
    }

    @Unique
    private static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
