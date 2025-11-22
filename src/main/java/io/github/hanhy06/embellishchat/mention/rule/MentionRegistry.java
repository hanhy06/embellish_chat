package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import io.github.hanhy06.embellishchat.util.TeamColorUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;

import java.util.*;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private final Config config;
    private final Style stylePreset;
    private final EnumMap<MentionType, Function<MentionParameter, Target>> registries;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.config = config;
        this.stylePreset = Style.EMPTY.withColor(config.mentionColor().getRGB());
        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.INSIDE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP),
                entry(MentionType.WORLD,this::WORLD)
        ));
    }

    public Function<MentionParameter, Target> get(MentionType key){
        return registries.get(key);
    }

    private Target EVERYONE(MentionParameter parameter){
        List<ServerPlayerEntity> players = manager.getPlayerList();
        
        return new Target(players,stylePreset);
    }

    private Target INSIDE(MentionParameter parameter){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.option())
        ).stream().toList();

        return new Target(players,stylePreset);
    }

    private Target TEAM(MentionParameter parameter){
        Team team = scoreboard.getTeam(parameter.option());
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

        return new Target(players, style);
    }


    private Target PLAYER(MentionParameter parameter){
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
                    .withParent(stylePreset)
                    .withColor(TeamColorUtil.getPlayerColor(
                            scoreboard,
                            parameter.option(),
                            config.mentionColor().getRGB()
                    ));
        }

        return new Target(players, style);
    }

    private Target LUCK_PERMS_GROUP(MentionParameter parameter){
        List<ServerPlayerEntity> players = new ArrayList<>();

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return new Target(players, stylePreset);
        }

        players = LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayerList());

        return new Target(players, stylePreset);
    }

    private Target WORLD(MentionParameter parameter){
        String worldName = parameter.option();
        MinecraftServer server = parameter.sender().getEntityWorld().getServer();
        ServerWorld targetWorld = null;

        for (ServerWorld world : server.getWorlds()) {
            String id = world.getRegistryKey().getValue().toString();
            if (id.equals(worldName) || world.getRegistryKey().getValue().getPath().equals(worldName)) {
                targetWorld = world;
                break;
            }
        }

        List<ServerPlayerEntity> players = new ArrayList<>();

        if (targetWorld != null) {
            players = PlayerLookup.world(targetWorld).stream().toList();
        } else {
            EmbellishChat.LOGGER.info("World " + worldName + " not found. @world mention ignored.");
        }

        return new Target(players, stylePreset);
    }
}
