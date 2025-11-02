package com.hanhy06.embellish_chat.util;

import com.hanhy06.embellish_chat.EmbellishChat;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.server.network.ServerPlayerEntity;

public class LuckPermsUtil {
    public static boolean getNotification(ServerPlayerEntity player) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return true;
            }

            String notificationValue = user
                    .getCachedData()
                    .getMetaData()
                    .getMetaValue("embellish-chat-notification");

            return notificationValue == null || "true".equals(notificationValue);
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms is present but not ready for metadata checks.", e);
            return true;
        }
    }

    public static boolean setNotification(ServerPlayerEntity player,boolean bool){
        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return false;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return false;
            }

            user.data().add(Node.builder("embellish-chat-notification." + bool).build());
            luckPerms.getUserManager().saveUser(user);
            return true;
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms is present but not ready for metadata set.", e);
            return false;
        }
    }
}
