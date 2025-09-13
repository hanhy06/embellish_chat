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

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget chatField;
    @Shadow
    ChatInputSuggestor chatInputSuggestor;
    @Shadow
    public void sendMessage(String chatText, boolean addToHistory) {};
    @Shadow
    public void setChatFromHistory(int offset) {};

    @Unique
    private final List<Integer> newLinePositions = new ArrayList<>();
    @Unique
    private int lastCursorX = -1;

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            this.lastCursorX = -1;
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && hasShiftDown()) {
            newLinePositions.add(chatField.getCursor());
            this.lastCursorX = -1;
            newLinePositions.sort(null);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.sendMessage(this.chatField.getText(), true);
            this.client.setScreen(null);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP && hasShiftDown()) {
            this.lastCursorX = -1;
            this.setChatFromHistory(-1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN && hasShiftDown()) {
            this.lastCursorX = -1;
            this.setChatFromHistory(1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            List<String> line = splitToLines(chatField.getText(),newLinePositions);
            CursorPosition pos = convertCursorTo2D(line,chatField.getCursor());

            if (this.lastCursorX == -1) {
                this.lastCursorX = pos.column();
            }

            int y = Math.max(0, pos.line() - 1);
            int x = Math.min(this.lastCursorX, line.get(y).length());

            this.chatField.setCursor(convertCursorTo1D(line,y,x),false);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            List<String> line = splitToLines(chatField.getText(),newLinePositions);
            CursorPosition pos = convertCursorTo2D(line,chatField.getCursor());
            
            if (this.lastCursorX == -1) {
                this.lastCursorX = pos.column();
            }

            int y = Math.min(pos.line() + 1, line.size() - 1);
            int x = Math.min(this.lastCursorX, line.get(y).length());

            this.chatField.setCursor(convertCursorTo1D(line,y,x),false);
            return true;
        }
        else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        } else {
            boolean isHandled = this.chatField.keyPressed(keyCode, scanCode, modifiers);
            if (isHandled) {
                this.lastCursorX = -1;
            }
            return isHandled;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);

        List<String> currentLines = splitToLines(this.chatField.getText(),newLinePositions);

        int lineCount = currentLines.size();
        int topY = this.height - 14 - (lineCount - 1) * 9;
        context.fill(
                2, topY - 2,
                this.width - 2, this.height - 2,
                this.client.options.getTextBackgroundColor(Integer.MIN_VALUE)
        );
        
        int lineHeight = 9;
        for (int i = 0; i < currentLines.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, currentLines.get(i), 4, topY + (i * lineHeight), 0xFFFFFFFF);
        }

        if (this.client.inGameHud.getTicks() / 6 % 2 == 0) {
            CursorPosition cursorPos = convertCursorTo2D(currentLines, this.chatField.getCursor());

            String currentLineText = currentLines.get(cursorPos.line());
            int cursorRenderX = 4 + this.textRenderer.getWidth(currentLineText.substring(0, cursorPos.column()));
            int cursorRenderY = topY + (cursorPos.line() * lineHeight);
            context.fill(cursorRenderX, cursorRenderY - 1, cursorRenderX + 1, cursorRenderY + lineHeight, 0xFFE0E0E0);
        }

        this.chatInputSuggestor.render(context, mouseX, mouseY);
        MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt(mouseX, mouseY);
        if (messageIndicator != null && messageIndicator.text() != null) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(messageIndicator.text(), 210), mouseX, mouseY);
        } else {
            Style style = this.client.inGameHud.getChatHud().getTextStyleAt(mouseX, mouseY);
            if (style != null && style.getHoverEvent() != null) {
                context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
            }
        }
    }

    @Unique
    private List<String> splitToLines(String str,List<Integer> positions){
        List<String> currentLines = new ArrayList<>();
        int lastPos = 0;

        for (int pos : positions) {
            if (pos > str.length()) continue;
            String tmp = str.substring(lastPos, pos);
            currentLines.add(tmp.isEmpty() ? "":tmp);
            lastPos = pos;
        }
        currentLines.add(str.substring(lastPos));
        return currentLines;
    }

    @Unique
    private CursorPosition convertCursorTo2D(List<String> lines, int cursorPosition) {
        int textLengthCounter = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineLength = line.length();
            if (cursorPosition <= textLengthCounter + lineLength) {
                int column = cursorPosition - textLengthCounter;
                return new CursorPosition(i, column);
            }
            textLengthCounter += lineLength;
        }
        return new CursorPosition(lines.size() - 1, lines.get(lines.size() - 1).length());
    }

    @Unique
    private int convertCursorTo1D(List<String> lines, int line, int column) {
        int position = 0;
        for (int i = 0; i < line; i++) {
            position += lines.get(i).length();
        }
        position += column;
        return position;
    }

    protected ChatScreenMixin(Text title) {
        super(title);
    }
}
