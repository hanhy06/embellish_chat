package com.hanhy06.embellish_chat.mention;

import com.hanhy06.embellish_chat.config.Config;
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

public class MentionProcessor {
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

    public MentionProcessor(PlayerManager manager, Scoreboard scoreboard){
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

    public void broadcastMention(ServerPlayerEntity sender, List<MentionTarget> mentionTargets){
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

        for (MentionTarget mentionTarget : mentionTargets){
            if (mentionTarget.players() == null || mentionTarget.players().isEmpty()) continue;

            for (ServerPlayerEntity player : mentionTarget.players()){
                player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,mentionPitch);
                player.sendMessage(titleText ,true);
            }
        }
    }

    public List<ParsedMention> parseMentions(String raw){
        Matcher matcher = MENTION_PATTERN.matcher(raw);

        List<ParsedMention> parsedMentions = new ArrayList<>();
        while (matcher.find()){
            parsedMentions.add(new ParsedMention(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return parsedMentions;
    }

    public List<MentionTarget> processReceiver(ServerPlayerEntity sender, List<ParsedMention> parsedMentions){
        List<MentionTarget> mentionTargets = new ArrayList<>();
        boolean canGroupMention = canUseGroupMention(sender);

        for (ParsedMention parsedMention : parsedMentions){
            String name = parsedMention.name();

            if (canGroupMention){
                switch (name) {
                    case "everyone" -> mentionTargets.add(targetEveryone(parsedMention));
                    case "here" -> mentionTargets.add(targetHere(sender, parsedMention));
                    case "team" -> mentionTargets.add(targetTeam(sender, parsedMention));
                    default -> mentionTargets.add(targetPlayer(parsedMention));
                }
            }else {
                mentionTargets.add(targetPlayer(parsedMention));
            }
        }

        return mentionTargets;
    }

    private MentionTarget targetEveryone(ParsedMention parsedMention){
        return new MentionTarget(
                "everyone",
                parsedMention.begin(),
                parsedMention.end(),
                groupMentionColor,
                manager.getPlayerList()
        );
    }

    private MentionTarget targetHere(ServerPlayerEntity sender, ParsedMention parsedMention){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                sender.getEntityWorld(),
                sender.getEntityPos(),
                hereRadius
        ).stream().toList();

        return new MentionTarget(
                "here",
                parsedMention.begin(),
                parsedMention.end(),
                groupMentionColor,
                players
        );
    }

    private MentionTarget targetTeam(ServerPlayerEntity sender, ParsedMention parsedMention){
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

            return new MentionTarget(
                    "team",
                    parsedMention.begin(),
                    parsedMention.end(),
                    color,
                    players
            );
        }else {
            return new MentionTarget(
                    "team",
                    parsedMention.begin(),
                    parsedMention.end(),
                    groupMentionColor,
                    null
            );
        }
    }

    private MentionTarget targetPlayer(ParsedMention parsedMention){
        ServerPlayerEntity player = manager.getPlayer(parsedMention.name());

        int teamColor;
        if (player != null){
            teamColor = TeamColor.getPlayerColor(player);
        }else if (offlineColorEnabled) {
            teamColor = TeamColor.getPlayerColor(scoreboard, parsedMention.name());
        } else {
            teamColor = mentionColor;
        }

        return new MentionTarget(
                parsedMention.name(),
                parsedMention.begin(),
                parsedMention.end(),
                teamColor,
                player != null ? List.of(player) : null
        );
    }

    private boolean canUseGroupMention(ServerPlayerEntity player){
        return !groupMentionOpOnly || manager.isOperator(player.getPlayerConfigEntry());
    }
}