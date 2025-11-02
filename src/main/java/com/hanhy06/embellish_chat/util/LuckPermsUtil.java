package com.hanhy06.embellish_chat.util;

import com.hanhy06.embellish_chat.EmbellishChat;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LuckPermsUtil {
    public static List<String> getPermissions(ServerPlayerEntity player, String permission, Set<String> permissions){
        List<String> result = new ArrayList<>();
        result.add(permission);
        if (player == null) return result;

        if (FabricLoader.getInstance().isModLoaded("luckperms")) {
            result.addAll(getLuckPerms(player,permissions));
        }else {
            result.addAll(getPermissionsAPI(player,permissions));
        }

        return result;
    }

    public static boolean getNotification(ServerPlayerEntity player) {
        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return true;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return true;
            }

            String notificationValue = user
                    .getCachedData()
                    .getMetaData()
                    .getMetaValue("meta.embellish-chat-notification");

            return notificationValue == null || "true".equals(notificationValue);
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms is present but not ready for metadata checks.", e);
            return true;
        }
    }

    public static boolean setNotification(ServerPlayerEntity player,boolean bool){
        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            return true;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return false;
            }

            user.data().add(Node.builder("meta.embellish-chat-notification." + bool).build());
            luckPerms.getUserManager().saveUser(user);
            return true;
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms is present but not ready for metadata set.", e);
            return false;
        }
    }

    private static List<String> getPermissionsAPI(ServerPlayerEntity player, Set<String> permissions){
        List<String> result = new ArrayList<>();

        for (String key : permissions){
            if (Permissions.check(player,key)) result.add(key);
        }

        return result;
    }

    private static List<String> getLuckPerms(ServerPlayerEntity player, Set<String> permissions){
        List<String> result = new ArrayList<>();

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());

            if (user == null) {
                return result;
            }

            for (String key : permissions){
                if (user.getCachedData().getPermissionData().checkPermission(key).asBoolean()) {
                    result.add(key);
                }
            }
        } catch (IllegalStateException e) {
            EmbellishChat.LOGGER.warn("LuckPerms is present but not ready for permission checks.", e);
        }

        return result;
    }
}
