package io.github.hanhy06.embellishchat.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class InventoryUtil {
    private static final HashMap<UUID, PlayerInventory> inventories = new HashMap<>();

    public static void put(UUID uuid,PlayerInventory inventory){
        inventories.put(uuid,inventory);
    }

    public static PlayerInventory get(UUID uuid){
        return inventories.get(uuid);
    }

    public static void registryLeaveEvent(){
        ServerPlayerEvents.LEAVE.register(player ->{
            inventories.remove(player.getUuid());
        });
    }
}
