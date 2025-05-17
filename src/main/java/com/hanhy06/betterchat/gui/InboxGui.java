package com.hanhy06.betterchat.gui;

import com.hanhy06.betterchat.mention.MentionData;
import com.hanhy06.betterchat.playerdata.PlayerDataManager;
import com.hanhy06.betterchat.preparation.Markdown;
import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class InboxGui {
    private final PlayerDataManager manager;
    private final Markdown markdown;

    public InboxGui(PlayerDataManager manager, Markdown markdown) {
        this.manager = manager;
        this.markdown = markdown;
    }

    public SimpleInventory createInboxGui(GameProfile profile, int pageNumber){
        SimpleInventory inventory = new SimpleInventory(54);

        List<MentionData> mentionData = manager.getMentionData(profile.getId(),pageNumber);

        inventory.setStack(4, createPlayerHead(profile));
        for (int i = 0 ;i < 9;i++){
            inventory.setStack(9+i,new ItemStack(Items.PURPLE_STAINED_GLASS_PANE));
        }
        for (int i = 1 ;i<=3 ;i++){
            for(int j=1 ; j<=7 ; j++){
                MentionData data = mentionData.get(i*j-1);

                ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
                WrittenBookContentComponent component = new WrittenBookContentComponent(
                        RawFilteredPair.of(""),
                        manager.getPlayerData(data.sender()).getPlayerName(),
                        0,
                        List.of(RawFilteredPair.of(markdown.markdown(Text.literal(data.originalText()),new ArrayList<>()))),
                        true
                );

                stack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, component);
                inventory.setStack(9*i+j,stack);
            }
        }

        return inventory;
    }

    private ItemStack createPlayerHead(GameProfile profile){
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        if (profile != null){
            ProfileComponent component = new ProfileComponent(profile);
            itemStack.set(DataComponentTypes.PROFILE,component);
        }
        return itemStack;
    }
}
