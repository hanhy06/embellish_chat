package io.github.hanhy06.embellishchat.inventory;

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private static final HashMap<UUID, InventoryContext> inventories = new HashMap<>();

    public static void putInventory(ServerPlayerEntity player) {
        SimpleInventory inventory = createInventoryLayout(player);
        Text name = Text.literal(player.getName().getString()+"'s inventory");
        inventories.put(player.getUuid(), new InventoryContext(inventory,name));
    }

    public static void putItem(ServerPlayerEntity player, ItemStack item){
        SimpleInventory inventory = createItemLayout(item.copy());
        Text name = item.getName();
        inventories.put(player.getUuid(), new InventoryContext(inventory,name));
    }

    public static void putEnderChest(ServerPlayerEntity player){
        SimpleInventory inventory = createEnderChestLayout(player);
        Text name = Text.literal(player.getName().getString()+"'s ender chest");
        inventories.put(player.getUuid(), new InventoryContext(inventory,name));
    }

    public static SimpleNamedScreenHandlerFactory get(UUID uuid){
        InventoryContext context = inventories.get(uuid);
        if (context == null) return null;

        SimpleInventory inventory = context.inventory();
        SimpleNamedScreenHandlerFactory factory;

        if (inventory.size() == 57) {
            factory = new SimpleNamedScreenHandlerFactory((id, playerInventory, player) ->
                    new InventoryScreenHandler(ScreenHandlerType.GENERIC_9X6,id,playerInventory,inventory,6),
                    context.name()
            );
        }else {
            factory = new SimpleNamedScreenHandlerFactory((id, playerInventory, player) ->
                    new InventoryScreenHandler(ScreenHandlerType.GENERIC_9X3,id,playerInventory,inventory,3),
                    context.name()
            );
        }

        return factory;
    }

    private static SimpleInventory createInventoryLayout(ServerPlayerEntity player){
        SimpleInventory inventory = new SimpleInventory(54);
        PlayerInventory playerInventory = player.getInventory();

        ItemStack grayPane = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        grayPane.set(DataComponentTypes.TOOLTIP_DISPLAY,new TooltipDisplayComponent(true, ReferenceSortedSets.emptySet()));
        for (int i = 0; i < 54; i++) {
            inventory.setStack(i, grayPane.copy());
        }

        inventory.setStack(6,player.getOffHandStack());
        for (int i = 0; i < 4; i++) {
            ItemStack item = playerInventory.getStack(39-i).copy();
            inventory.setStack(i+2, item);
        }
        for (int i = 9; i < 36; i++) {
            ItemStack item = playerInventory.getStack(i).copy();
            inventory.setStack(i + 9, item);
        }
        for (int i = 0; i < 9; i++) {
            ItemStack item = playerInventory.getStack(i).copy();
            inventory.setStack(i + 45, item);
        }

        return inventory;
    }

    private static SimpleInventory createItemLayout(ItemStack item){
        SimpleInventory inventory = new SimpleInventory(27);
        inventory.setStack(13,item);
        return inventory;
    }

    private static SimpleInventory createEnderChestLayout(ServerPlayerEntity player){
        SimpleInventory inventory = new SimpleInventory(27);
        EnderChestInventory enderChest = player.getEnderChestInventory();

        for (int i=0;i<27;i++){
            inventory.setStack(i,enderChest.getStack(i).copy());
        }

        return inventory;
    }

    public static void registryLeaveEvent(){
        ServerPlayerEvents.LEAVE.register(player ->{
            inventories.remove(player.getUuid());
        });
    }
}
