package io.github.hanhy06.embellish_chat.mixin;

import io.github.hanhy06.embellish_chat.styling.StylingProcessor;
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

        text = StylingProcessor.INSTANCE.applyStylingRule(text.copy(),"embellish_chat.command_argument");

        cir.setReturnValue(text);
    }
}
