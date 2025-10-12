package com.hanhy06.embellish_chat.mixin;


import com.hanhy06.embellish_chat.chat.processor.StyledTextProcessor;
import com.hanhy06.embellish_chat.config.ConfigManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @ModifyArg(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
            ),
            index = 1
    )
    public ItemStack updateResult(ItemStack itemStack) {
        Text newName = itemStack.getName();
        if (ConfigManager.getConfig().inAnvilStylingEnabled()){
            newName = StyledTextProcessor.applyStyles(newName.copy());
            itemStack.set(DataComponentTypes.CUSTOM_NAME,newName);
        }
        return itemStack;
    }
}
