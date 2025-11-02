package com.hanhy06.embellish_chat.util;

import com.hanhy06.embellish_chat.EmbellishChat;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LuckPermsUtil {
    public static List<String> getPermissions(ServerPlayerEntity player, String permission, Set<String> permissions){
        List<String> result = new ArrayList<>();
        result.add(permission);
        if (player == null) return result;

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            result.addAll(getPermissionsAPI(player,permissions));
        }else {
            result.addAll(getLuckPerms(player,permissions));
        }

        return result;
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
