package io.github.hanhy06.embellishchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import io.github.hanhy06.embellishchat.styling.StyleProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;

@Mixin(ComponentArgument.class)
public class TextArgumentTypeMixin {
    @Inject(method = "getRawComponent", at = @At("RETURN"), cancellable = true)
    private static void getTextArgument(
            CommandContext<CommandSourceStack> context,
            String name,
            CallbackInfoReturnable<Component> cir
    ) {
        Component text = cir.getReturnValue();

        text = StyleProcessor.INSTANCE.handleStyle(text.copy(), List.of("embellish-chat.command_argument"),context.getSource().getPlayer());

        cir.setReturnValue(text);
    }
}
