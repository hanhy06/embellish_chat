package com.hanhy06.embellish_chat.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermissionUtil {
    public static List<String> getPermissionsKeys(ServerPlayerEntity player, String defaultKey, Set<String> keySet){
        List<String> keys = new ArrayList<>();
        keys.add(defaultKey);

        for (String key : keySet){
            if (Permissions.check(player,key)) keys.add(key);
        }

        return keys;
    }
}
