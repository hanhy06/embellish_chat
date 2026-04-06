package io.github.hanhy06.embellishchat.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class CartographyTableScreenHandler extends CartographyTableMenu {
    public CartographyTableScreenHandler(int containerId, Inventory inventory) {
        super(containerId, inventory);
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, @NonNull ContainerInput containerInput, @NonNull Player player) {
        if (slotIndex >= 0 && slotIndex < 3) return;
        if (containerInput == ContainerInput.PICKUP_ALL) return;

        super.clicked(slotIndex, buttonNum, containerInput, player);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    @Override
    public void removed(@NonNull Player player) {
        this.container.clearContent();
        super.removed(player);
    }
}
