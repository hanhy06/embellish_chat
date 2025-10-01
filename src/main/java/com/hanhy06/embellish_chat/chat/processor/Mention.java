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

import java.util.*;
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
        Scoreboard scoreboard = server.getScoreboard();

        Matcher matcher = MENTION_PATTERN.matcher(raw);

        while (matcher.find()){
            receivers.add(
                    new Receiver(
                            matcher.group(1),
                            matcher.start(),
                            matcher.end(1),
                            TeamColor.getPlayerColor(scoreboard,matcher.group(1))
                    )
            );
        }

        return  receivers;
    }
}