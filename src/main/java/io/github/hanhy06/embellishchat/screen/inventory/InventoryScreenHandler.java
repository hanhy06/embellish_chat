package io.github.hanhy06.embellishchat.screen.inventory;

import io.github.hanhy06.embellishchat.screen.CartographyTableScreenHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InventoryScreenHandler extends ChestMenu {
    public InventoryScreenHandler(MenuType<?> type,int syncId, Inventory playerInventory, Container inventory,int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        if (slotIndex >= 0 && slotIndex < 9 * this.getRowCount()) {
            ItemStack stack = this.getSlot(slotIndex).getItem();

            if (player instanceof ServerPlayer serverPlayer) {
                if (stack.getItem() == Items.WRITTEN_BOOK) openWrittenBook(stack,serverPlayer);
                else if (stack.has(DataComponents.MAP_ID)) openMapImage(stack,serverPlayer);
            }

            return;
        }
        if (actionType == ClickType.PICKUP_ALL) return;

        super.clicked(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }


    private static void openWrittenBook(ItemStack stack,ServerPlayer player){
        player.closeContainer();

        int selectedHotbarSlot = player.getInventory().getSelectedSlot();
        ItemStack original = player.getInventory().getItem(selectedHotbarSlot).copy();
        int playerInventorySlot = InventoryMenu.USE_ROW_SLOT_START + selectedHotbarSlot;
        ItemStack book = stack.copy();

        player.connection.send(new ClientboundContainerSetSlotPacket(
                InventoryMenu.CONTAINER_ID,
                player.inventoryMenu.incrementStateId(),
                playerInventorySlot,
                book
        ));
        player.openItemGui(book, InteractionHand.MAIN_HAND);
        player.connection.send(new ClientboundContainerSetSlotPacket(
                InventoryMenu.CONTAINER_ID,
                player.inventoryMenu.incrementStateId(),
                playerInventorySlot,
                original
        ));
    }

    private static void openMapImage(ItemStack stack, ServerPlayer player) {
        player.closeContainer();

        player.openMenu(new SimpleMenuProvider(
                (syncId, playerInventory, screenPlayer) -> {
                    CartographyTableScreenHandler menu = new CartographyTableScreenHandler(
                            syncId,
                            playerInventory
                    );

                    menu.container.setItem(0, stack.copy());
                    menu.slotsChanged(menu.container);
                    return menu;
                },
                stack.getDisplayName()
        ));
    }
}
