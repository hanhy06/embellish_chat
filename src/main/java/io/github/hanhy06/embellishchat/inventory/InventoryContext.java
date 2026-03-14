package io.github.hanhy06.embellishchat.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;

public record InventoryContext(SimpleContainer inventory, Component name) {
}
