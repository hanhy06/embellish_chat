package com.hanhy06.betterchat.mixin.client;

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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && hasShiftDown()) {
            newLinePositions.add(chatField.getCursor());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.sendMessage(this.chatField.getText(), true);
            this.client.setScreen(null);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP && hasShiftDown()) {
            this.setChatFromHistory(-1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN && hasShiftDown()) {
            this.setChatFromHistory(1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            List<String> currentLines = splitToLines(this.chatField.getText(), newLinePositions);
            if (currentLines.size() <= 1) {
                this.chatField.setCursor(0, false);
                return true;
            }
            int[] cursorInfo = getCursorLineAndColumn(currentLines, this.chatField.getCursor());
            int cursorLine = cursorInfo[0];
            int cursorColumn = cursorInfo[1];

            if (cursorLine > 0) {
                int targetLineIndex = cursorLine - 1;
                int targetColumn = Math.min(cursorColumn, currentLines.get(targetLineIndex).length());
                int newCursorPos = currentLines.stream().limit(targetLineIndex).mapToInt(String::length).sum() + targetColumn;
                this.chatField.setCursor(newCursorPos, false);
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            List<String> currentLines = splitToLines(this.chatField.getText(), newLinePositions);
            if (currentLines.size() <= 1) {
                this.chatField.setCursor(this.chatField.getText().length(), false);
                return true;
            }
            int[] cursorInfo = getCursorLineAndColumn(currentLines, this.chatField.getCursor());
            int cursorLine = cursorInfo[0];
            int cursorColumn = cursorInfo[1];

            if (cursorLine < currentLines.size() - 1) {
                int targetLineIndex = cursorLine + 1;
                int targetColumn = Math.min(cursorColumn, currentLines.get(targetLineIndex).length());
                int newCursorPos = currentLines.stream().limit(targetLineIndex).mapToInt(String::length).sum() + targetColumn;
                this.chatField.setCursor(newCursorPos, false);
            }
            return true;
        }
        else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);

        newLinePositions.sort(null);

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
            int[] cursorInfo = getCursorLineAndColumn(currentLines, this.chatField.getCursor());
            int cursorLine = cursorInfo[0];
            int cursorColumn = cursorInfo[1];
            String currentLineText = currentLines.get(cursorLine);
            int cursorRenderX = 4 + this.textRenderer.getWidth(currentLineText.substring(0, cursorColumn));
            int cursorRenderY = topY + (cursorLine * lineHeight);
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
            currentLines.add(str.substring(lastPos, pos));
            lastPos = pos;
        }
        currentLines.add(str.substring(lastPos));
        return currentLines;
    }

    @Unique
    private int[] getCursorLineAndColumn(List<String> lines, int cursorIndex) {
        int cursorLine = 0;
        int cursorColumn = 0;
        int textLengthCounter = 0;

        for (int i = 0; i < lines.size(); i++) {
            int lineLength = lines.get(i).length();
            if (cursorIndex <= textLengthCounter + lineLength) {
                cursorLine = i;
                cursorColumn = cursorIndex - textLengthCounter;
                break;
            }
            textLengthCounter += lineLength;
        }
        return new int[]{cursorLine, cursorColumn};
    }
    
    protected ChatScreenMixin(Text title) {
        super(title);
    }
}
