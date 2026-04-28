package io.github.hanhy06.embellishchat.util;

import io.github.hanhy06.embellishchat.config.ConfigManager;
import me.wesley1808.advancedchat.impl.channels.Channels;
import me.wesley1808.advancedchat.impl.channels.ChatChannel;
import me.wesley1808.advancedchat.impl.data.AdvancedChatData;
import me.wesley1808.advancedchat.impl.data.DataManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;

public class AdvancedChatUtil {
    public static HashSet<ServerPlayer> getChannelPlayers(String targetChannel, ServerPlayer sender, List<ServerPlayer> players) {
        HashSet<ServerPlayer> result = new HashSet<>();

        ChatChannel senderChannel = DataManager.get(sender).channel;
        ChatChannel channel = Channels.get(targetChannel);
        if (channel == null) throw new MessageBlockedException("Channel not found.");

        if (ConfigManager.getConfig().require_same_channel() && !channel.equals(senderChannel)) {
            throw new MessageBlockedException("You must be in the same channel.");
        }

        for (ServerPlayer player : players) {
            AdvancedChatData data = DataManager.get(player);
            if (data.channel == channel) {
                result.add(player);
            }
        }

        return result;
    }
}
