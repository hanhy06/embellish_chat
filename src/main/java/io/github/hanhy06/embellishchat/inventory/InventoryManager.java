package io.github.hanhy06.embellishchat.inventory;

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private static final HashMap<UUID, InventoryContext> inventories = new HashMap<>();

    public static void putInventory(ServerPlayer player) {
        SimpleContainer inventory = createInventoryLayout(player);
        Component name = Component.literal(player.getName().getString()+"'s inventory");
        inventories.put(player.getUUID(), new InventoryContext(inventory,name));
    }

    public static void putItem(ServerPlayer player, ItemStack item){
        SimpleContainer inventory = createItemLayout(item.copy());
        Component name = item.getHoverName();
        inventories.put(player.getUUID(), new InventoryContext(inventory,name));
    }

    public static void putEnderChest(ServerPlayer player){
        SimpleContainer inventory = createEnderChestLayout(player);
        Component name = Component.literal(player.getName().getString()+"'s ender chest");
        inventories.put(player.getUUID(), new InventoryContext(inventory,name));
    }

    public static SimpleMenuProvider get(UUID uuid){
        InventoryContext context = inventories.get(uuid);
        if (context == null) return null;

        SimpleContainer inventory = context.inventory();
        SimpleMenuProvider factory;

        if (inventory.getContainerSize() == 54) {
            factory = new SimpleMenuProvider((id, playerInventory, player) ->
                    new InventoryScreenHandler(MenuType.GENERIC_9x6,id,playerInventory,inventory,6),
                    context.name()
            );
        }else {
            factory = new SimpleMenuProvider((id, playerInventory, player) ->
                    new InventoryScreenHandler(MenuType.GENERIC_9x3,id,playerInventory,inventory,3),
                    context.name()
            );
        }

        return factory;
    }

    private static SimpleContainer createInventoryLayout(ServerPlayer player){
        SimpleContainer inventory = new SimpleContainer(54);
        Inventory playerInventory = player.getInventory();

        ItemStack grayPane = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        grayPane.set(DataComponents.TOOLTIP_DISPLAY,new TooltipDisplay(true, ReferenceSortedSets.emptySet()));
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, grayPane.copy());
        }

        inventory.setItem(6,player.getOffhandItem().copy());
        for (int i = 0; i < 4; i++) {
            ItemStack item = playerInventory.getItem(39-i).copy();
            inventory.setItem(i+2, item);
        }
        for (int i = 9; i < 36; i++) {
            ItemStack item = playerInventory.getItem(i).copy();
            inventory.setItem(i + 9, item);
        }
        for (int i = 0; i < 9; i++) {
            ItemStack item = playerInventory.getItem(i).copy();
            inventory.setItem(i + 45, item);
        }

        return inventory;
    }

    private static SimpleContainer createItemLayout(ItemStack item){
        SimpleContainer inventory = new SimpleContainer(27);
        inventory.setItem(13,item);
        return inventory;
    }

    private static SimpleContainer createEnderChestLayout(ServerPlayer player){
        SimpleContainer inventory = new SimpleContainer(27);
        PlayerEnderChestContainer enderChest = player.getEnderChestInventory();

        for (int i=0;i<27;i++){
            inventory.setItem(i,enderChest.getItem(i).copy());
        }

        return inventory;
    }

    public static void registerLeaveEvent(){
        ServerPlayerEvents.LEAVE.register(player ->{
            inventories.remove(player.getUUID());
        });
    }
}
