package com.hanhy06.betterchat.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Playerhead {
    public static ItemStack createPlayerHeadItemData(GameProfile profile){
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        if (profile != null){
            ProfileComponent component = new ProfileComponent(profile);
            itemStack.set(DataComponentTypes.PROFILE,component);
        }
        return itemStack;
    }
}
