package io.github.hanhy06.embellishchat.inventory;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
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
                ItemStack original = player.getUseItem();

                int selectSlot = player.getInventory().getSelectedSlot() + 36;
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                        0, this.incrementStateId(), selectSlot, stack
                ));
                serverPlayer.connection.send(new ClientboundOpenBookPacket(InteractionHand.MAIN_HAND));
                serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                        0, this.incrementStateId(), selectSlot, original
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
