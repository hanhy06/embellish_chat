package com.hanhy06.embellish_chat.mention.rule;

import com.hanhy06.embellish_chat.config.Config;
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
    private final Style mentionStyle;
    private final EnumMap<MentionType, Function<MentionParameter,ParsedTarget>> registries;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.config = config;
        this.mentionStyle = Style.EMPTY.withBold(true).withColor(config.mentionColor());
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
        return ParsedTarget.of(
                manager.getPlayerList(),
                mentionStyle
        );
    }

    private ParsedTarget INSIDE(MentionParameter parameter){
        return ParsedTarget.of(
                PlayerLookup.around(
                        parameter.sender().getEntityWorld(),
                        parameter.sender().getEntityPos(),
                        Float.parseFloat(parameter.mention())
                ).stream().toList(),
                mentionStyle
        );
    }

    private ParsedTarget OUTSIDE(MentionParameter parameter){
        Collection<ServerPlayerEntity> insides = PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.mention())
        );
        List<ServerPlayerEntity> outsides = manager.getPlayerList();
        outsides.removeAll(insides);

        return ParsedTarget.of(
                outsides,
                mentionStyle
        );
    }

    private ParsedTarget TEAM(MentionParameter parameter){
        Team team = parameter.sender().getScoreboardTeam();

        if (team != null){
            return ParsedTarget.of(
                    team
                            .getPlayerList()
                            .stream()
                            .map(manager::getPlayer)
                            .filter(Objects::nonNull)
                            .toList(),
                    team.getDisplayName().getStyle()
            );
        }else {
            return ParsedTarget.of(
                    List.of(),
                    mentionStyle
            );
        }
    }


    private ParsedTarget PLAYER(MentionParameter parameter){
        ServerPlayerEntity target = manager.getPlayer(parameter.mention());

        if (target!=null){
            Style style = target.getDisplayName().getStyle();
            return ParsedTarget.of(
                    List.of(target),
                    style
            );
        }else {
            return ParsedTarget.of(
                    List.of(),
                    mentionStyle.withColor(
                            TeamColor.getPlayerColor(
                                    scoreboard,
                                    parameter.mention(),
                                    config.mentionColor()
                                    )
                    )
            );
        }
    }

    private ParsedTarget LUCK_PERMS_GROUP(MentionParameter parameter){
        return ParsedTarget.of(
                List.of(),
                mentionStyle
        );
    }
}
