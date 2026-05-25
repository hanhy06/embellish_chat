package io.github.hanhy06.embellishchat.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class CartographyTableScreenHandler extends CartographyTableMenu {
    public CartographyTableScreenHandler(int containerId, Inventory inventory) {
        super(containerId, inventory);
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ClickType clickType, Player player) {
        if (slotIndex >= 0 && slotIndex < 3) return;
        if (clickType == ClickType.PICKUP_ALL) return;

        super.clicked(slotIndex, buttonNum, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        this.container.clearContent();
        super.removed(player);
    }
}
