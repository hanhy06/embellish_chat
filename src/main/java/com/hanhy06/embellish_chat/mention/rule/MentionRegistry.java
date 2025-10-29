package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.mention.data.ParsedMention;
import com.hanhy06.embellish_chat.mention.data.ParsedTarget;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.*;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private final Config config;
    private final Style stylePreset;
    private final EnumMap<MentionType, Function<MentionParameter,ParsedTarget>> registries;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.config = config;
        this.stylePreset = Style.EMPTY.withColor(config.mentionColor());
        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.HERE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
                entry(MentionType.OUTSIDE,this::OUTSIDE),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP)
        ));
    }

    public Function<MentionParameter,ParsedTarget> get(MentionType key){
        return registries.get(key);
    }

    private ParsedTarget EVERYONE(MentionParameter parameter){
        ParsedMention mention = parameter.parsedMention();
        List<ServerPlayerEntity> players = manager.getPlayerList();

        return ParsedTarget.of(
                mention,
                players,
                stylePreset
        );
    }

    private ParsedTarget INSIDE(MentionParameter parameter){
        ParsedMention mention = parameter.parsedMention();
        List<ServerPlayerEntity> players = PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.mention())
        ).stream().toList();

        return ParsedTarget.of(
                mention,
                players,
                stylePreset
        );
    }

    private ParsedTarget OUTSIDE(MentionParameter parameter){
        ParsedMention parsedMention = parameter.parsedMention();
        List<ServerPlayerEntity> outsides = manager.getPlayerList();
        outsides.removeAll(INSIDE(parameter).players());

        return ParsedTarget.of(
                parsedMention,
                outsides,
                stylePreset
        );
    }

    private ParsedTarget TEAM(MentionParameter parameter){
        Team team = parameter.sender().getScoreboardTeam();
        ParsedMention parsedMention = parameter.parsedMention();
        List<ServerPlayerEntity> players = new ArrayList<>();
        Style style = stylePreset;

        if (team != null){
            players = team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
            style = team
                    .getDisplayName()
                    .getStyle()
                    .withParent(style);
        }

        return ParsedTarget.of(
                parsedMention,
                players,
                style
        );
    }


    private ParsedTarget PLAYER(MentionParameter parameter){
        ParsedMention parsedMention = parameter.parsedMention();
        ServerPlayerEntity target = manager.getPlayer(parameter.mention());
        Style style = stylePreset;
        List<ServerPlayerEntity> players = new ArrayList<>();

        if (target!=null){
            style = target
                    .getDisplayName()
                    .getStyle()
                    .withParent(style);
            players.add(target);
        }else {
            style = Style.EMPTY
                    .withParent(stylePreset)
                    .withColor(TeamColor.getPlayerColor(
                            scoreboard,
                            parameter.mention(),
                            config.mentionColor())
                    );
        }

        return ParsedTarget.of(
                parsedMention,
                players,
                style
        );
    }

    private ParsedTarget LUCK_PERMS_GROUP(MentionParameter parameter){
        return ParsedTarget.of(
                parameter.parsedMention(),
                new ArrayList<>(),
                stylePreset
        );
    }
}
