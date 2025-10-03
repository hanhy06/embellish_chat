package com.hanhy06.embellish_chat.chat.processor;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private static PlayerManager manager;
    private static Scoreboard scoreboard;

    private final SoundEvent mentionSound;
    private final float mentionPitch;

    public Mention(MinecraftServer server,SoundEvent mentionSound,float mentionPitch){
        manager = server.getPlayerManager();
        scoreboard = server.getScoreboard();
        this.mentionSound = mentionSound;
        this.mentionPitch = mentionPitch;
    }

    public void broadcastMention(ServerPlayerEntity sender, List<Receiver> receivers){
        MutableText titleText = sender.getName().copy();
        titleText.styled(style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender)));
        titleText.append(
                Text.literal(" mentioned you").styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );

        for (Receiver receiver : new HashSet<>(receivers)){
            ServerPlayerEntity player = receiver.player();
            if (player == null) continue;

            player.playSoundToPlayer(mentionSound,SoundCategory.PLAYERS,1f,mentionPitch);
            player.sendMessage(titleText ,true);
        }
    }

    public List<Receiver> parseMentions(String raw){
        List<Receiver> receivers = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(raw);

        while (matcher.find()){
            String name = matcher.group(1);
            ServerPlayerEntity player = manager.getPlayer(name);
            int teamColor;

            if (player != null){
                teamColor = TeamColor.getPlayerColor(player);
            }else {
                teamColor = TeamColor.getPlayerColor(scoreboard,name);
            }

            receivers.add(
                    new Receiver(
                            name,
                            matcher.start(),
                            matcher.end(1),
                            teamColor,
                            player
                    )
            );
        }

        return  receivers;
    }
}