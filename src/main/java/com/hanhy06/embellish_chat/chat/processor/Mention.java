package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.data.Target;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private int defaultGroupMentionColor;
    private double defaultHereRadius;
    private boolean groupMentionOpOnly;

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
        this.defaultGroupMentionColor = config.defaultGroupMentionColor();
        this.defaultHereRadius = config.defaultHereRadius();
        this.groupMentionOpOnly = config.groupMentionOpOnly();
    }

    public void broadcastMention(ServerPlayerEntity sender, List<Receiver> receivers){
        MutableText titleText = sender.getName().copy();
        titleText.styled(style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender)));
        titleText.append(
                Text.literal(mentionMessage).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );

        for (Receiver receiver:receivers){
            if (receiver.players() == null || receiver.players().isEmpty()) continue;

            for (ServerPlayerEntity player : receiver.players()){
                player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,mentionPitch);
                player.sendMessage(titleText ,true);
            }
        }
    }

    public List<Target> parseMentions(String raw){
        Matcher matcher = MENTION_PATTERN.matcher(raw);

        List<Target> targets = new ArrayList<>();
        while (matcher.find()){
            targets.add(new Target(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return  targets;
    }

    public List<Receiver> processReceiver(ServerPlayerEntity sender, List<Target> targets){
        List<Receiver> receivers = new ArrayList<>();
        boolean canGroupMention = canUseGroupMention(sender);

        for (Target target : targets){
            String name = target.name();

            if (canGroupMention){
                switch (name) {
                    case "everyone" -> receivers.add(targetEveryone(target));
                    case "here" -> receivers.add(targetHere(sender,target));
                    case "team" -> receivers.add(targetTeam(sender,target));
                    default -> receivers.add(targetPlayer(target));
                }
            }else {
                receivers.add(targetPlayer(target));
            }
        }

        return receivers;
    }

    private Receiver targetEveryone(Target target){
        return new Receiver(
                "everyone",
                target.begin(),
                target.end(),
                defaultGroupMentionColor,
                manager.getPlayerList()
        );
    }

    private Receiver targetHere(ServerPlayerEntity sender,Target target){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                sender.getEntityWorld(),
                sender.getEntityPos(),
                defaultHereRadius
        ).stream().toList();

        return new Receiver(
                "here",
                target.begin(),
                target.end(),
                defaultGroupMentionColor,
                players
        );
    }

    private Receiver targetTeam(ServerPlayerEntity sender,Target target){
        Team team = sender.getScoreboardTeam();
        if (team != null){
            List<ServerPlayerEntity> players = team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();

            return new Receiver(
                    "team",
                    target.begin(),
                    target.end(),
                    defaultGroupMentionColor,
                    players
            );
        }else {
            return new Receiver(
                    "team",
                    target.begin(),
                    target.end(),
                    defaultGroupMentionColor,
                    null
            );
        }
    }

    private Receiver targetPlayer(Target target){
        ServerPlayerEntity player = manager.getPlayer(target.name());

        int teamColor;
        if (player != null){
            teamColor = TeamColor.getPlayerColor(player);
        }else if (offlineColorEnabled) {
            teamColor = TeamColor.getPlayerColor(scoreboard, target.name());
        } else {
            teamColor = defaultMentionColor;
        }

        return new Receiver(
                target.name(),
                target.begin(),
                target.end(),
                teamColor,
                player != null ? List.of(player) : null
        );
    }

    private boolean canUseGroupMention(ServerPlayerEntity player){
        return !groupMentionOpOnly || manager.isOperator(player.getPlayerConfigEntry());
    }
}