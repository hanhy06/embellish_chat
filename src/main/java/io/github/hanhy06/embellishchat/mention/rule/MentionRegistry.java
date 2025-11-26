package io.github.hanhy06.embellishchat.mention.rule;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.util.ColorUtil;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
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
    private final EnumMap<MentionType, Function<MentionParameter, List<ServerPlayerEntity>>> registries;

    public MentionRegistry(Config config, PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.config = config;
        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.INSIDE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP),
                entry(MentionType.WORLD,this::WORLD)
        ));
    }

    public Function<MentionParameter, List<ServerPlayerEntity>> get(MentionType key){
        return registries.get(key);
    }

    private List<ServerPlayerEntity> EVERYONE(MentionParameter parameter){
        return manager.getPlayerList();
    }

    private List<ServerPlayerEntity> INSIDE(MentionParameter parameter){
        return PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.option())
        ).stream().toList();
    }

    private List<ServerPlayerEntity> TEAM(MentionParameter parameter){
        Team team = scoreboard.getTeam(parameter.option());
        List<ServerPlayerEntity> players = new ArrayList<>();

        if (team != null){
            players = team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
        }

        return players;
    }


    private List<ServerPlayerEntity> PLAYER(MentionParameter parameter){
        ServerPlayerEntity target = manager.getPlayer(parameter.option());

        return target == null ? new ArrayList<>() : List.of(target);
    }

    private List<ServerPlayerEntity> LUCK_PERMS_GROUP(MentionParameter parameter){
        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return List.of();
        }

        return LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayerList());
    }

    private List<ServerPlayerEntity> WORLD(MentionParameter parameter){
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

        return players;
    }
}
