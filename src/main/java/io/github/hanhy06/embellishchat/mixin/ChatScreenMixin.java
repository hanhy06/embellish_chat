package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.suggestion.SuggestionManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow protected EditBox input;
    @Unique private SuggestionManager suggestions;

    @Inject(method = "init",at = @At("TAIL"))
    protected void init(CallbackInfo ci) {
        this.suggestions = new SuggestionManager(input,(ChatScreen)(Object) this);
    }

    @Inject(method = "onEdited", at = @At("TAIL"))
    private void onEdited(String value, CallbackInfo ci) {
        this.suggestions.update();
    }

    @Inject(method = "keyPressed", at=@At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir){
        if (suggestions.keyPressed(event)) cir.setReturnValue(true);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    public void mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (suggestions.mouseScrolled(scrollY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci){
        suggestions.render(graphics);
    }
}
