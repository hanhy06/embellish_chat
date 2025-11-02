package io.github.hanhy06.embellish_chat.util;

import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PermissionUtil {
    public static List<String> getPermissions(ServerPlayerEntity player, Set<String> permissions){
        List<String> result = new ArrayList<>();
        if (player == null) return result;

        for (String key : permissions){
            if (Permissions.check(player,key)) result.add(key);
        }

        return result;
    }

    public static void registerPermissions(){
        PermissionCheckEvent.EVENT.register((source, permission) -> {
            if (permission.equals("embellish_chat.chat")) {
                return TriState.TRUE;
            }
            if (permission.equals("embellish_chat.mention")) {
                return TriState.TRUE;
            }
            return TriState.DEFAULT;
        });

    }
}
