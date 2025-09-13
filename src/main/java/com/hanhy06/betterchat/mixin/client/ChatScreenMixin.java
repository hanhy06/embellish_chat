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
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
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
    @Shadow
    public void sendMessage(String chatText, boolean addToHistory){};

    @Unique
    private int cursorX;
    @Unique
    private int cursorY;

    @Unique
    private final List<String> lines = new ArrayList<>();

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
        this.chatField.setVisible(false);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        String currentLine = this.lines.get(this.cursorY);
        String newLine = currentLine.substring(0, this.cursorX) + chr + currentLine.substring(this.cursorX);
        this.lines.set(this.cursorY, newLine);
        this.cursorX++;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_V && hasControlDown()) {
            this.insertText(this.client.keyboard.getClipboard());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hasShiftDown()) {
                String currentLine = this.lines.get(this.cursorY);
                String textAfterCursor = currentLine.substring(this.cursorX);
                this.lines.set(this.cursorY, currentLine.substring(0, this.cursorX));
                this.cursorY++;
                this.lines.add(this.cursorY, textAfterCursor);
                this.cursorX = 0;
            } else {
                String message = String.join("\n", this.lines).trim();
                if (!message.isBlank()) {
                    this.sendMessage(message,true);
                }
                this.client.setScreen(null);
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            String currentLine = this.lines.get(this.cursorY);
            String textAfterCursor = currentLine.substring(this.cursorX);
            this.lines.set(this.cursorY, currentLine.substring(0, this.cursorX));
            this.cursorY++;
            this.lines.add(this.cursorY, textAfterCursor);
            this.cursorX = 0;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            this.cursorX = Math.max(0, this.cursorX - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            this.cursorX = Math.min(this.lines.get(this.cursorY).length(), this.cursorX + 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            if (this.cursorY > 0) {
                this.cursorY--;
                this.cursorX = Math.min(this.lines.get(this.cursorY).length(), this.cursorX);
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if (this.cursorY < this.lines.size() - 1) {
                this.cursorY++;
                this.cursorX = Math.min(this.lines.get(this.cursorY).length(), this.cursorX);
            }
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }


    @Unique
    private void insertText(String text) {
        String currentLine = this.lines.get(this.cursorY);
        String textToInsert = text.split("\\R")[0];
        String newLine = currentLine.substring(0, this.cursorX) + textToInsert + currentLine.substring(this.cursorX);
        this.lines.set(this.cursorY, newLine);
        this.cursorX += textToInsert.length();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);
        int lineCount = Math.max(1, this.lines.size());
        int inputBoxHeight = 9 * lineCount + 3;
        int yPos = this.height - 14 - inputBoxHeight + 12;

        context.fill(2, yPos, this.width - 2, this.height - 2, MinecraftClient.getInstance().options.getTextBackgroundColor(0.8F));

        for (int i = 0; i < this.lines.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, this.lines.get(i), 4, yPos + (i * 9) + 2, 0xFFFFFFFF);
        }

        if (this.client.inGameHud.getTicks() / 6 % 2 == 0) {
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

    protected ChatScreenMixin(Text title) {
        super(title);
    }
}
