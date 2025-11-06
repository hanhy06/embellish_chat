package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

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
            EmbellishChat.LOGGER.warn("LuckPerms API is not ready. Failed to check notification metadata.", e);
            return true;
        }
    }

    public static void setNotification(ServerPlayerEntity player, boolean bool){
        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return;
            }

            user.data().remove(MetaNode.builder("embellish-chat-notification",String.valueOf(!bool)).build());
            user.data().add(MetaNode.builder("embellish-chat-notification",String.valueOf(bool)).build());
            luckPerms.getUserManager().saveUser(user);
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms API is not ready. Failed to set notification metadata.", e);
        }
    }

    public static List<ServerPlayerEntity> getGroupPlayers(String targetGroup, List<ServerPlayerEntity> players){
        List<ServerPlayerEntity> result = new ArrayList<>();

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            result = players.stream()
                    .filter(player -> {
                        User user = luckPerms.getUserManager().getUser(player.getUuid());
                        return user != null && targetGroup.equals(user.getPrimaryGroup());
                    })
                    .toList();

        } catch (IllegalStateException exception) {
            EmbellishChat.LOGGER.warn("LuckPerms API is not ready. Failed to process @group mention.", exception);
        }

        return result;
    }
}
