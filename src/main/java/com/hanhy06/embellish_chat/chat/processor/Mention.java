package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public static void broadcastMention(Identifier mentionSound, ServerPlayerEntity sender, List<Receiver> receivers){
        PlayerManager manager = EmbellishChat.server.getPlayerManager();

        for (Receiver receiver : new HashSet<>(receivers)){
            ServerPlayerEntity player = manager.getPlayer(receiver.name());
            if (player == null) continue;

            MutableText titleText = sender.getName().copy()
                    .styled(style -> style.withColor(receiver.teamColor()).withBold(true))
                    .append(Text.literal(" mentioned you").fillStyle(Style.EMPTY.withBold(false).withColor(Formatting.WHITE)));

            player.playSoundToPlayer(SoundEvent.of(mentionSound),SoundCategory.UI,1f,1.75f);
            player.sendMessage(titleText ,true);
        }
    }

    public static List<Receiver> parseMentions(MinecraftServer server, String raw){
        List<Receiver> receivers = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(raw);

        Scoreboard scoreboard = server.getScoreboard();
        PlayerManager manager = server.getPlayerManager();


        while (matcher.find()){
            String name = matcher.group(1);
            ServerPlayerEntity player = manager.getPlayer(name);

            int teamColor;
            boolean isOnline;

            if (player != null){
                teamColor = TeamColor.getPlayerColor(player);
                isOnline = true;
            }else {
                teamColor = TeamColor.getPlayerColor(scoreboard,name);
                isOnline = false;
            }

            receivers.add(
                    new Receiver(
                            name,
                            matcher.start(),
                            matcher.end(1),
                            teamColor,
                            isOnline
                    )
            );
        }

        return  receivers;
    }
}