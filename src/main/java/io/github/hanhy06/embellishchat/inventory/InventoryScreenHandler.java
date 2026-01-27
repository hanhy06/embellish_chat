package io.github.hanhy06.embellishchat.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class InventoryScreenHandler extends GenericContainerScreenHandler {
    public InventoryScreenHandler(ScreenHandlerType<?> type,int syncId, PlayerInventory playerInventory, Inventory inventory,int raws) {
        super(type, syncId, playerInventory, inventory, raws);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < 9 * this.getRows()) {
            ItemStack stack = this.getSlot(slotIndex).getStack();

            if (stack.getItem() == Items.WRITTEN_BOOK && player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack original = player.getActiveItem();

                int selectSlot = player.getInventory().getSelectedSlot() + 36;
                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
                        0, this.nextRevision(), selectSlot, stack
                ));
                serverPlayer.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(Hand.MAIN_HAND));
                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
                        0, this.nextRevision(), selectSlot, original
                ));
            }

            return;
        }
        if (actionType.equals(SlotActionType.PICKUP_ALL)) return;

        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
