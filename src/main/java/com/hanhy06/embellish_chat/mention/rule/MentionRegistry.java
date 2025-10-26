package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.config.Config;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

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

    private List<ServerPlayerEntity> EVERYONE(MentionParameter parameter){
        return manager.getPlayerList();
    }

    private List<ServerPlayerEntity> HERE(MentionParameter parameter){
        return PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                config.hereRadius()
        ).stream().toList();
    }

    private List<ServerPlayerEntity> TEAM_SELF(MentionParameter parameter){
        Team team = parameter.sender().getScoreboardTeam();

        if (team != null){
            return team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
        }else {
            return List.of();
        }
    }

    private List<ServerPlayerEntity> TEAM_OTHER(MentionParameter parameter){
        Team team = scoreboard.getTeam(parameter.name());

        if (team != null){
            return team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
        }else {
            return List.of();
        }
    }

    private List<ServerPlayerEntity> PLAYER(MentionParameter parameter){
        ServerPlayerEntity receiver = manager.getPlayer(parameter.name());

        if (receiver!=null){
            return List.of(receiver);
        }else {
            return List.of();
        }
    }

    private List<ServerPlayerEntity> LUCK_PERMS_GROUP(MentionParameter parameter){
        return List.of();
    }
}
