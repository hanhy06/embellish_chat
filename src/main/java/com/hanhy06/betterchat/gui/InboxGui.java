package com.hanhy06.betterchat.gui;

import com.hanhy06.betterchat.mention.MentionData;
import com.hanhy06.betterchat.playerdata.PlayerData;
import com.hanhy06.betterchat.playerdata.PlayerDataManager;
import com.hanhy06.betterchat.preparation.Markdown;
import com.mojang.authlib.GameProfile;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
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

        ItemStack glassPane = new ItemStack(Items.PURPLE_STAINED_GLASS_PANE);
        glassPane.set(DataComponentTypes.CUSTOM_NAME, Text.literal(""));
        for (int k = 0; k < 9; k++) {
            inventory.setStack(9 + k, glassPane);
        }

        int bookDataIndex = 0;
        for (int bookRow = 0; bookRow < 3; bookRow++) {
            for (int bookCol = 0; bookCol < 7; bookCol++) {
                if (bookDataIndex >= mentionData.size()) {
                    continue;
                }

                MentionData data = mentionData.get(bookDataIndex);
                PlayerData senderData = manager.getPlayerData(data.sender());
                String playerName = (senderData != null) ? senderData.getPlayerName() : "Unknown Player";

                MutableText markdownOriginalText = markdown.markdown(Text.literal(data.originalText()), new ArrayList<>());

                List<RawFilteredPair<Text>> pages = List.of(
                        RawFilteredPair.of(
                                Text.literal(playerName + " : \n").append(markdownOriginalText)
                        )
                );

                WrittenBookContentComponent bookContent = new WrittenBookContentComponent(
                        RawFilteredPair.of(""),
                        playerName,
                        0,
                        pages,
                        true
                );

                ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);
                bookStack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, bookContent);

                int inventorySlot = (2 + bookRow) * 9 + bookCol;
                inventory.setStack(inventorySlot, bookStack);

                bookDataIndex++;
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
