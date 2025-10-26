package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.mention.MentionTarget;
import com.hanhy06.embellish_chat.mention.ParsedMention;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;

public class MentionRegistry {
    private final Config config;
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.config = config;
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    private MentionTarget EVERYONE(ParsedMention parsedMention){
        return new MentionTarget(
                "everyone",
                parsedMention.begin(),
                parsedMention.end(),
                config.groupMentionColor(),
                manager.getPlayerList()
        );
    }

    private MentionTarget HERE(ServerPlayerEntity sender, ParsedMention parsedMention){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                sender.getEntityWorld(),
                sender.getEntityPos(),
                config.hereRadius()
        ).stream().toList();

        return new MentionTarget(
                "here",
                parsedMention.begin(),
                parsedMention.end(),
                config.groupMentionColor(),
                players
        );
    }

    private MentionTarget TEAM_SELF(ServerPlayerEntity sender, ParsedMention parsedMention){
        Team team = sender.getScoreboardTeam();

        if (team != null){
            int color = config.groupMentionColor();
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
                    config.groupMentionColor(),
                    null
            );
        }
    }

    private MentionTarget PLAYER(ParsedMention parsedMention){
        ServerPlayerEntity player = manager.getPlayer(parsedMention.name());

        int teamColor;
        if (player != null){
            teamColor = TeamColor.getPlayerColor(player);
        }else if (config.offlineColorEnabled()) {
            teamColor = TeamColor.getPlayerColor(scoreboard, parsedMention.name());
        } else {
            teamColor = config.mentionColor();
        }

        return new MentionTarget(
                parsedMention.name(),
                parsedMention.begin(),
                parsedMention.end(),
                teamColor,
                player != null ? List.of(player) : null
        );
    }
}
