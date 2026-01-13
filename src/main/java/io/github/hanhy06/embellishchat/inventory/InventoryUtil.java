package io.github.hanhy06.embellishchat.inventory;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

public class InventoryUtil {
    private static final HashMap<UUID, SimpleInventory> inventories = new HashMap<>();

    public static void put(ServerPlayerEntity player, PlayerInventory playerInventory) {
        ItemStack grayPane = new ItemStack(net.minecraft.item.Items.GRAY_STAINED_GLASS_PANE);
        grayPane.set(DataComponentTypes.CUSTOM_NAME, Text.empty());

        SimpleInventory simpleInventory = new SimpleInventory(54);
        for (int i = 0; i < 18; i++) {
            simpleInventory.setStack(i, grayPane.copy());
        }

        simpleInventory.setStack(0,player.getOffHandStack());
        simpleInventory.setStack(1,player.getMainHandStack());
        for (int i = 0; i < 4; i++) {
            ItemStack item = playerInventory.getStack(i).copy();
            simpleInventory.setStack(i + 5, item);
        }
        for (int i = 9; i < 36; i++) {
            ItemStack item = playerInventory.getStack(i).copy();
            simpleInventory.setStack(i + 9, item);
        }
        for (int i = 0; i < 9; i++) {
            ItemStack item = playerInventory.getStack(i).copy();
            simpleInventory.setStack(i + 45, item);
        }

        inventories.put(player.getUuid(), simpleInventory);
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
