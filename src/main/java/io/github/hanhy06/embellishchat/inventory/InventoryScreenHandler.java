package io.github.hanhy06.embellishchat.inventory;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

public class InventoryScreenHandler extends ChestMenu {
    public InventoryScreenHandler(MenuType<?> type,int syncId, Inventory playerInventory, Container inventory,int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    @Override
    public void clicked(int slotIndex, int button, @NonNull ContainerInput actionType, @NonNull Player player) {
        if (slotIndex >= 0 && slotIndex < 9 * this.getRowCount()) {
            ItemStack stack = this.getSlot(slotIndex).getItem();

            if (stack.getItem() == Items.WRITTEN_BOOK && player instanceof ServerPlayer serverPlayer) {
                int selectedHotbarSlot = player.getInventory().getSelectedSlot();
                ItemStack original = player.getInventory().getItem(selectedHotbarSlot).copy();
                int playerInventorySlot = InventoryMenu.USE_ROW_SLOT_START + selectedHotbarSlot;
                ItemStack book = stack.copy();

                serverPlayer.closeContainer();

                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                        InventoryMenu.CONTAINER_ID,
                        serverPlayer.inventoryMenu.incrementStateId(),
                        playerInventorySlot,
                        book
                ));
                serverPlayer.openItemGui(book, InteractionHand.MAIN_HAND);
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                        InventoryMenu.CONTAINER_ID,
                        serverPlayer.inventoryMenu.incrementStateId(),
                        playerInventorySlot,
                        original
                ));
            }

            return;
        }
        if (actionType == ContainerInput.PICKUP_ALL) return;

        super.clicked(slotIndex, button, actionType, player);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }
}
