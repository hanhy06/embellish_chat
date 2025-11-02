package com.hanhy06.embellish_chat.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermissionUtil {
    public static List<String> getPermissions(ServerPlayerEntity player, String permission, Set<String> permissions){
        List<String> result = new ArrayList<>();
        result.add(permission);
        if (player == null) return result;

        for (String key : permissions){
            if (Permissions.check(player,key)) result.add(key);
        }

        return result;
    }
}
