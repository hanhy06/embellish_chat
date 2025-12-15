package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LuckPermsUtil {
    public static List<ServerPlayerEntity> getGroupPlayers(String targetGroup, List<ServerPlayerEntity> players){
        List<ServerPlayerEntity> result = new ArrayList<>();

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            for (ServerPlayerEntity player : players) {
                User user = luckPerms.getUserManager().getUser(player.getUuid());
                if (user == null) continue;

                for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
                    if (group.getName().equals(targetGroup)) {
                        result.add(player);
                        break;
                    }
                }
            }
        } catch (IllegalStateException exception) {
            EmbellishChat.LOGGER.warn("LuckPerms API is not ready. Failed to process @group mention.", exception);
        }

        return result;
    }
}
