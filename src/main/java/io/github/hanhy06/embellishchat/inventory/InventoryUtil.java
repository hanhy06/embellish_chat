package io.github.hanhy06.embellishchat.inventory;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class InventoryUtil {
    private static final HashMap<UUID, SimpleInventory> inventories = new HashMap<>();

    public static void put(UUID uuid,PlayerInventory playerInventory){
        SimpleInventory simpleInventory = new SimpleInventory(54);

        for (int i=9;i<36;i++){
            ItemStack item = playerInventory.getStack(i);
            simpleInventory.setStack(i,item);
        }

        inventories.put(uuid,simpleInventory);
    }

    public static SimpleInventory get(UUID uuid){
        return inventories.get(uuid);
    }

    public static void registryLeaveEvent(){
        ServerPlayerEvents.LEAVE.register(player ->{
            inventories.remove(player.getUuid());
        });
    }
}
