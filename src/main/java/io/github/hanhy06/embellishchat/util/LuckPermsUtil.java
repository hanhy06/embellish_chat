package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LuckPermsUtil {
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
