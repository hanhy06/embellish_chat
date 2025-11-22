package io.github.hanhy06.embellishchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextArgumentType.class)
public class TextArgumentTypeMixin {
    @Inject(method = "getTextArgument", at = @At("RETURN"), cancellable = true)
    private static void getTextArgument(
            CommandContext<ServerCommandSource> context,
            String name,
            CallbackInfoReturnable<Text> cir
    ) {
        Text text = cir.getReturnValue();

        text = StylingProcessor.INSTANCE.handleStyle(text.copy(), List.of("embellish-chat.command_argument"),context.getSource().getPlayer());

        cir.setReturnValue(text);
    }
}
