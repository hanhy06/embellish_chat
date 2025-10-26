package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.config.Config;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final Config config;
    private final PlayerManager manager;
    private final Scoreboard scoreboard;
    private final EnumMap<MentionType, Function<MentionParameter,List<ServerPlayerEntity>>> registries;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.config = config;
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.HERE,this::HERE),
                entry(MentionType.TEAM_SELF,this::TEAM_SELF),
                entry(MentionType.TEAM_OTHER,this::TEAM_OTHER),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP)
        ));
    }

    public Function<MentionParameter,List<ServerPlayerEntity>> get(MentionType key){
        return registries.get(key);
    }

    private List<ServerPlayerEntity> EVERYONE(MentionParameter parameter){
        return manager.getPlayerList();
    }

    private List<ServerPlayerEntity> HERE(MentionParameter parameter){
        return PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.mention())
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
        Team team = scoreboard.getTeam(parameter.mention());

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
        ServerPlayerEntity receiver = manager.getPlayer(parameter.mention());

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
