package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private SoundEvent mentionSound;
    private float mentionPitch;
    private String mentionMessage;
    private boolean offlineColorEnabled;
    private int defaultMentionColor;

    public Mention(PlayerManager manager,Scoreboard scoreboard){
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    public void updateConfig(Config config){
        this.mentionSound = SoundEvent.of(Identifier.tryParse(config.defaultMentionSound()));
        this.mentionPitch = config.defaultMentionPitch();
        this.mentionMessage = config.defaultMentionMessage();
        this.offlineColorEnabled = config.offlineColorEnabled();
        this.defaultMentionColor = config.defaultMentionColor();
    }

    public void broadcastMention(ServerPlayerEntity sender, List<Receiver> receivers){
        MutableText titleText = sender.getName().copy();
        titleText.styled(style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender)));
        titleText.append(
                Text.literal(mentionMessage).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );

        for (Receiver receiver : new HashSet<>(receivers)){
            ServerPlayerEntity player = receiver.player();
            if (player == null) continue;

            player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,mentionPitch);
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
            }else if (offlineColorEnabled) {
                teamColor = TeamColor.getPlayerColor(scoreboard,name);
            } else {
                teamColor = defaultMentionColor;
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