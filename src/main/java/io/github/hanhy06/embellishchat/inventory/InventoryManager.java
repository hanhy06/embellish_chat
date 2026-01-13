package io.github.hanhy06.embellishchat.inventory;

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private static final HashMap<UUID, SimpleInventory> inventories = new HashMap<>();

    public static void put(ServerPlayerEntity player) {
        SimpleInventory inventory = createInventoryLayout(player);

        inventories.put(player.getUuid(), inventory);
    }

    public static SimpleInventory get(UUID uuid){
        return inventories.get(uuid);
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

    public static void registryLeaveEvent(){
        ServerPlayerEvents.LEAVE.register(player ->{
            inventories.remove(player.getUuid());
        });
    }
}
