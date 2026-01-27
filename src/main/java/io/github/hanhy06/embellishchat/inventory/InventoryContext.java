package io.github.hanhy06.embellishchat.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;

public record InventoryContext(SimpleInventory inventory, Text name) {
}
