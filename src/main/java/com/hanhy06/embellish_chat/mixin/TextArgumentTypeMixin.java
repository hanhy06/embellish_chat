package com.hanhy06.embellish_chat.mixin;

import com.hanhy06.embellish_chat.chat.ChatHandler;
import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextArgumentType.class)
public class TextArgumentTypeMixin {
    @Inject(method = "getTextArgument", at = @At("RETURN"), cancellable = true)
    private static void getTextArgument(
            CommandContext<ServerCommandSource> context,
            String name,
            CallbackInfoReturnable<Text> cir
    ) {
        Text text = cir.getReturnValue();
        if (ConfigManager.getConfig().inCommandStylingEnabled()){
            text = StyledTextProcessor.applyStyles(text.copy());
        }
        cir.setReturnValue(text);
    }
}
