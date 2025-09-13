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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget chatField;
    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // chatField를 더 이상 숨기지 않습니다.
        // chatField가 모든 로직을 처리하도록 두고, 우리는 렌더링만 가로챕니다.
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // 모든 문자 입력을 chatField에 위임합니다.
        return this.chatField.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hasShiftDown()) {
                // Shift+Enter: chatField에 줄바꿈 문자를 삽입하도록 요청합니다.
                this.chatField.write("\n");
                return true;
            }
        }

        // Shift+Enter를 제외한 모든 키 입력(Ctrl+A, Ctrl+V, Enter, 화살표 등)을
        // chatField에 위임하여 기본 기능을 그대로 사용합니다.
        if (this.chatField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);

        // chatField에서 현재 텍스트를 가져와 줄 단위로 분리합니다.
        List<String> currentLines = Arrays.asList(this.chatField.getText().split("\\n"));
        int lineCount = Math.max(1, currentLines.size());

        // 렌더링 위치 계산
        final int lineHeight = 9;
        final int bottomMargin = 2;
        final int padding = 3;
        int inputBoxHeight = (lineHeight * lineCount) + padding;
        int topY = this.height - bottomMargin - inputBoxHeight;

        // 배경 렌더링
        context.fill(2, topY, this.width - 2, this.height - bottomMargin, this.client.options.getTextBackgroundColor(0.8F));

        // 텍스트 렌더링
        for (int i = 0; i < currentLines.size(); i++) {
            context.drawTextWithShadow(this.textRenderer, currentLines.get(i), 4, topY + (i * lineHeight) + 2, 0xFFFFFFFF);
        }

        // 커서 렌더링
        if (this.client.inGameHud.getTicks() / 6 % 2 == 0) {
            // chatField의 1차원 커서 위치를 2차원(줄, 칸) 위치로 변환합니다.
            int cursorIndex = this.chatField.getCursor();
            int cursorLine = 0;
            int cursorColumn = 0;
            int textLengthCounter = 0;

            for (int i = 0; i < currentLines.size(); i++) {
                int lineLength = currentLines.get(i).length();
                if (textLengthCounter + lineLength + 1 > cursorIndex) {
                    cursorLine = i;
                    cursorColumn = cursorIndex - textLengthCounter;
                    break;
                }
                textLengthCounter += lineLength + 1; // +1 for the newline character
            }

            String currentLineText = currentLines.get(cursorLine);
            int cursorRenderX = 4 + this.textRenderer.getWidth(currentLineText.substring(0, cursorColumn));
            int cursorRenderY = topY + (cursorLine * lineHeight) + 1;
            context.fill(cursorRenderX, cursorRenderY, cursorRenderX + 1, cursorRenderY + lineHeight, 0xFFE0E0E0);
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
