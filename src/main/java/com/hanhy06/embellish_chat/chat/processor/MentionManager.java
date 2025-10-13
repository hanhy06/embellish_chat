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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionManager {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private boolean groupMentionOpOnly;
    private boolean offlineColorEnabled;
    private int mentionColor;
    private int groupMentionColor;
    private SoundEvent mentionSound;
    private float mentionPitch;
    private String mentionTitlePrefix;
    private String mentionTitleSuffix;
    private double hereRadius;

    public MentionManager(PlayerManager manager, Scoreboard scoreboard){
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    public void updateConfig(Config config){
        this.groupMentionOpOnly = config.groupMentionOpOnly();
        this.offlineColorEnabled = config.offlineColorEnabled();
        this.mentionColor = config.mentionColor();
        this.groupMentionColor = config.groupMentionColor();
        this.mentionSound =  SoundEvent.of(Identifier.tryParse(config.mentionSound()));
        this.mentionPitch = config.mentionPitch();
        this.mentionTitlePrefix = config.mentionTitlePrefix();
        this.mentionTitleSuffix = config.mentionTitleSuffix();
        this.hereRadius = config.hereRadius();
    }

    public void broadcastMention(ServerPlayerEntity sender, List<Receiver> receivers){
        MutableText titleText = Text.empty();
        titleText.append(
                Text.literal(mentionTitlePrefix).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );
        titleText.append(
                sender.getName().copy().styled(
                        style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender))
                )
        );
        titleText.append(
                Text.literal(mentionTitleSuffix).styled(
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
                groupMentionColor,
                manager.getPlayerList()
        );
    }

    private Receiver targetHere(ServerPlayerEntity sender,Target target){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                sender.getEntityWorld(),
                sender.getEntityPos(),
                hereRadius
        ).stream().toList();

        return new Receiver(
                "here",
                target.begin(),
                target.end(),
                groupMentionColor,
                players
        );
    }

    private Receiver targetTeam(ServerPlayerEntity sender,Target target){
        Team team = sender.getScoreboardTeam();

        if (team != null){
            int color = groupMentionColor;
            Formatting formatting = team.getColor();
            if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                color = formatting.getColorValue();
            }

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
                    color,
                    players
            );
        }else {
            return new Receiver(
                    "team",
                    target.begin(),
                    target.end(),
                    groupMentionColor,
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
            teamColor = mentionColor;
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