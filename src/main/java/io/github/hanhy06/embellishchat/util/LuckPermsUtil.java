package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;

public class LuckPermsUtil {
    public static HashSet<ServerPlayer> getGroupPlayers(String targetGroup, List<ServerPlayer> players){
        HashSet<ServerPlayer> result = new HashSet<>();

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            for (ServerPlayer player : players) {
                User user = luckPerms.getUserManager().getUser(player.getUUID());
                if (user == null) continue;

                for (Group group : user.getInheritedGroups(user.getQueryOptions())) {
                    if (group.getName().equals(targetGroup)) {
                        result.add(player);
                        break;
                    }
                }
            }
        } catch (IllegalStateException exception) {
            EmbellishChat.LOGGER.warn("[embellish-chat/integration] LuckPerms API is not ready. Failed to process @group mention.", exception);
        }

        return result;
    }
}
