package com.hanhy06.betterchat.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Shadow
    protected TextFieldWidget chatField;
    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    @Unique
    private final List<String> lines = new ArrayList<>();
    @Unique
    private int cursorX;
    @Unique
    private int cursorY;
    @Unique
    private int selectionX;
    @Unique
    private int selectionY;
    @Unique
    private int tickCounter;
    @Unique
    private static final int MAX_LINES = 10;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.lines.clear();
        String initialText = this.chatField.getText();
        if (initialText.isEmpty()) {
            this.lines.add("");
        } else {
            this.lines.addAll(Arrays.asList(initialText.split("\\n")));
        }
        this.cursorY = this.lines.size() - 1;
        this.cursorX = this.lines.get(this.cursorY).length();
        clearSelection();
        this.chatField.setVisible(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickCounter++;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.client.inGameHud.getChatHud().render(context, this.tickCounter, mouseX, mouseY, true);
        int lineCount = Math.max(1, this.lines.size());
        int inputBoxHeight = 9 * lineCount + 3;
        int yPos = this.height - 14 - inputBoxHeight + 12;

        context.fill(2, yPos, this.width - 2, this.height - 2, MinecraftClient.getInstance().options.getTextBackgroundColor(0.8F));

        renderSelection(context, yPos);
        for (int i = 0; i < this.lines.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, this.lines.get(i), 4, yPos + (i * 9) + 2, 0xFFFFFFFF);
        }

        if (this.tickCounter / 6 % 2 == 0) {
            String currentLine = this.lines.get(this.cursorY);
            int cursorRenderX = 4 + this.textRenderer.getWidth(currentLine.substring(0, this.cursorX));
            int cursorRenderY = yPos + (this.cursorY * 9) + 1;
            context.fill(cursorRenderX, cursorRenderY, cursorRenderX + 1, cursorRenderY + 9, 0xFFE0E0E0);
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int lineCount = Math.max(1, this.lines.size());
        int inputBoxHeight = 9 * lineCount + 3;
        int yPos = this.height - 14 - inputBoxHeight + 12;
        if (mouseY >= yPos && mouseY <= yPos + inputBoxHeight) {
            int lineIndex = MathHelper.clamp((int)((mouseY - yPos - 1) / 9), 0, this.lines.size() - 1);
            int charIndex = this.textRenderer.trimToWidth(this.lines.get(lineIndex), (int)mouseX - 4).length();
            this.cursorY = lineIndex;
            this.cursorX = charIndex;
            if (!hasShiftDown()) {
                clearSelection();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        int lineCount = Math.max(1, this.lines.size());
        int inputBoxHeight = 9 * lineCount + 3;
        int yPos = this.height - 14 - inputBoxHeight + 12;
        if (mouseY >= yPos && mouseY <= yPos + inputBoxHeight) {
            int lineIndex = MathHelper.clamp((int)((mouseY - yPos - 1) / 9), 0, this.lines.size() - 1);
            int charIndex = this.textRenderer.trimToWidth(this.lines.get(lineIndex), (int)mouseX - 4).length();
            this.cursorY = lineIndex;
            this.cursorX = charIndex;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (hasControlDown()) {
            if (keyCode == GLFW.GLFW_KEY_A) { selectAll(); return true; }
            if (keyCode == GLFW.GLFW_KEY_C) { copy(); return true; }
            if (keyCode == GLFW.GLFW_KEY_V) { paste(); return true; }
        }
        if (handleKeyNavigation(keyCode)) return true;
        if (keyCode == GLFW.GLFW_KEY_ENTER && hasShiftDown()) {
            insertText("\n");
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String message = String.join("\n", this.lines).trim();
            if (!message.isEmpty()) {
                ((ChatScreen)(Object)this).sendMessage(message, true);
            }
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr != '\u0000') {
            insertText(String.valueOf(chr));
        }
        return true;
    }

    @Unique
    private void insertText(String text) {
        if (hasSelection()) deleteSelection();
        String[] newLines = text.split("\\r?\\n", -1);
        for (int i = 0; i < newLines.length; i++) {
            String line = newLines[i];
            String currentLine = this.lines.get(this.cursorY);
            String beforeCursor = currentLine.substring(0, this.cursorX);
            String afterCursor = currentLine.substring(this.cursorX);
            this.lines.set(this.cursorY, beforeCursor + line + afterCursor);
            this.cursorX += line.length();
            if (i < newLines.length - 1) {
                if (this.lines.size() >= MAX_LINES) break;
                currentLine = this.lines.get(this.cursorY);
                afterCursor = currentLine.substring(this.cursorX);
                this.lines.set(this.cursorY, currentLine.substring(0, this.cursorX));
                this.cursorY++;
                this.lines.add(this.cursorY, afterCursor);
                this.cursorX = 0;
            }
        }
        autoWrap();
        clearSelection();
        updateSuggestor();
    }

    @Unique
    private void autoWrap() {
        int maxWidth = this.width - 12;
        String currentLine = this.lines.get(this.cursorY);
        if (this.textRenderer.getWidth(currentLine) > maxWidth) {
            int lastSpace = currentLine.substring(0, this.cursorX).lastIndexOf(' ');
            if (lastSpace != -1 && lastSpace > 0) {
                if (this.lines.size() >= MAX_LINES) return;
                String beforeWrap = currentLine.substring(0, lastSpace);
                String afterWrap = currentLine.substring(lastSpace + 1);
                this.lines.set(this.cursorY, beforeWrap);
                this.cursorY++;
                this.lines.add(this.cursorY, afterWrap);
                this.cursorX = afterWrap.length();
            }
        }
    }

    @Unique
    private boolean handleKeyNavigation(int keyCode) {
        boolean shift = hasShiftDown();
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (hasSelection()) {
                deleteSelection();
            } else {
                if (this.cursorX > 0) {
                    String currentLine = this.lines.get(this.cursorY);
                    if (this.cursorX <= currentLine.length()) {
                        this.lines.set(this.cursorY, currentLine.substring(0, this.cursorX - 1) + currentLine.substring(this.cursorX));
                        this.cursorX--;
                    }
                } else if (this.cursorY > 0) {
                    String lineBelow = this.lines.remove(this.cursorY);
                    this.cursorY--;
                    String lineAbove = this.lines.get(this.cursorY);
                    this.cursorX = lineAbove.length();
                    this.lines.set(this.cursorY, lineAbove + lineBelow);
                }
            }
            updateSuggestor();
            return true;
        }
        if (keyCode >= 262 && keyCode <= 265) { // Arrow keys
            if (!shift) clearSelection();
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if (this.cursorX > 0) this.cursorX--;
            else if (this.cursorY > 0) { this.cursorY--; this.cursorX = this.lines.get(this.cursorY).length(); }
            if (!shift) clearSelection();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            String currentLine = this.lines.get(this.cursorY);
            if (this.cursorX < currentLine.length()) this.cursorX++;
            else if (this.cursorY < this.lines.size() - 1) { this.cursorY++; this.cursorX = 0; }
            if (!shift) clearSelection();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            if (this.cursorY > 0) { this.cursorY--; this.cursorX = Math.min(this.cursorX, this.lines.get(this.cursorY).length()); }
            if (!shift) clearSelection();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (this.cursorY < this.lines.size() - 1) { this.cursorY++; this.cursorX = Math.min(this.cursorX, this.lines.get(this.cursorY).length()); }
            if (!shift) clearSelection();
            return true;
        }
        return false;
    }

        @Unique
    private void updateSuggestor() {
        String fullText = String.join("
", this.lines);
        this.chatField.setText(fullText);

        int oneDimCursor = 0;
        for (int i = 0; i < this.cursorY; i++) {
            oneDimCursor += this.lines.get(i).length() + 1; // +1 for newline
        }
        oneDimCursor += this.cursorX;

        oneDimCursor = Math.min(oneDimCursor, fullText.length());
        this.chatField.setCursor(oneDimCursor);

        this.chatInputSuggestor.refresh();
    }

    @Unique
    private void clearSelection() {
        this.selectionX = this.cursorX;
        this.selectionY = this.cursorY;
    }

    @Unique
    private void selectAll() {
        this.selectionY = 0;
        this.selectionX = 0;
        this.cursorY = this.lines.size() - 1;
        this.cursorX = this.lines.get(this.cursorY).length();
    }

    @Unique
    private boolean hasSelection() {
        return this.cursorY != this.selectionY || this.cursorX != this.selectionX;
    }

    @Unique
    private String getSelection() {
        if (!hasSelection()) return "";
        int startLine, startX, endLine, endX;
        if (this.cursorY < this.selectionY || (this.cursorY == this.selectionY && this.cursorX < this.selectionX)) {
            startLine = this.cursorY; startX = this.cursorX; endLine = this.selectionY; endX = this.selectionX;
        } else {
            startLine = this.selectionY; startX = this.selectionX; endLine = this.cursorY; endX = this.cursorX;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startLine; i <= endLine; i++) {
            String line = this.lines.get(i);
            if (i == startLine && i == endLine) {
                sb.append(line, startX, endX);
            } else if (i == startLine) {
                sb.append(line.substring(startX)).append("\n");
            } else if (i == endLine) {
                sb.append(line, 0, endX);
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    @Unique
    private void deleteSelection() {
        if (!hasSelection()) return;
        int startLine, startX, endLine, endX;
        if (this.cursorY < this.selectionY || (this.cursorY == this.selectionY && this.cursorX < this.selectionX)) {
            startLine = this.cursorY; startX = this.cursorX; endLine = this.selectionY; endX = this.selectionX;
        } else {
            startLine = this.selectionY; startX = this.selectionX; endLine = this.cursorY; endX = this.cursorX;
        }
        String startLineText = this.lines.get(startLine);
        String endLineText = this.lines.get(endLine);

        startX = Math.min(startX, startLineText.length());
        endX = Math.min(endX, endLineText.length());

        String result = startLineText.substring(0, startX) + endLineText.substring(endX);
        for (int i = endLine; i >= startLine; i--) {
            this.lines.remove(i);
        }
        this.lines.add(startLine, result);
        this.cursorY = startLine;
        this.cursorX = startX;
        clearSelection();
    }

    @Unique
    private void copy() {
        if (hasSelection()) {
            this.client.keyboard.setClipboard(getSelection());
        }
    }

    @Unique
    private void paste() {
        String clipboard = this.client.keyboard.getClipboard();
        insertText(clipboard);
    }

    @Unique
    private void renderSelection(DrawContext context, int y) {
        if (!hasSelection()) return;
        int startLine, startX, endLine, endX;
        if (this.cursorY < this.selectionY || (this.cursorY == this.selectionY && this.cursorX < this.selectionX)) {
            startLine = this.cursorY; startX = this.cursorX; endLine = this.selectionY; endX = this.selectionX;
        } else {
            startLine = this.selectionY; startX = this.selectionX; endLine = this.cursorY; endX = this.cursorX;
        }
        for (int i = startLine; i <= endLine; i++) {
            String line = this.lines.get(i);
            int lineY = y + (i * 9) + 1;
            int lineStartRenderX = 4;
            int lineEndRenderX = 4 + this.textRenderer.getWidth(line);
            int highlightStartX = (i == startLine) ? 4 + this.textRenderer.getWidth(line.substring(0, startX)) : lineStartRenderX;
            int highlightEndX = (i == endLine) ? 4 + this.textRenderer.getWidth(line.substring(0, endX)) : lineEndRenderX;
            if (highlightStartX < highlightEndX) {
                context.fill(highlightStartX, lineY, highlightEndX, lineY + 9, 0x800000FF);
            }
        }
    }
}
