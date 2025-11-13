package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.ParsedMention;
import io.github.hanhy06.embellishchat.mention.data.ParsedTarget;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import io.github.hanhy06.embellishchat.util.TeamColorUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
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
        this.stylePreset = Style.EMPTY;
        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.INSIDE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
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
                Float.parseFloat(parameter.option())
        ).stream().toList();

        return ParsedTarget.of(
                mention,
                players,
                stylePreset
        );
    }

    private ParsedTarget TEAM(MentionParameter parameter){
        Team team = scoreboard.getTeam(parameter.option());
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
                    .getFormattedName()
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
        ServerPlayerEntity target = manager.getPlayer(parameter.option());
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
                    .withParent(stylePreset);
//                    .withColor(TeamColorUtil.getPlayerColor(
//                            scoreboard,
//                            parameter.option(),
//                            config.mentionColor())
//                    );
        }

        return ParsedTarget.of(
                parsedMention,
                players,
                style
        );
    }

    private ParsedTarget LUCK_PERMS_GROUP(MentionParameter parameter){
        ParsedMention parsedMention = parameter.parsedMention();
        List<ServerPlayerEntity> players = new ArrayList<>();

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return ParsedTarget.of(parsedMention, players, stylePreset);
        }

        players = LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayerList());

        return ParsedTarget.of(parsedMention, players, stylePreset);
    }
}
