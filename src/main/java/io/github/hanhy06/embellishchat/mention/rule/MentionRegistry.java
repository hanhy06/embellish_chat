package io.github.hanhy06.embellishchat.mention.rule;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.util.ColorUtil;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
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
    private final Style colorTeam;

    private final EnumMap<MentionType, Function<MentionParameter, Target>> registries;

    public MentionRegistry(Config config,PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        if (config.defaultTeamColor() != null){
            colorTeam = Style.EMPTY.withColor(config.defaultTeamColor().getRGB());
        }else {
            colorTeam = null;
        }

        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.INSIDE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP),
                entry(MentionType.WORLD,this::WORLD),
                entry(MentionType.CUSTOM,this::CUSTOM)
        ));
    }

    public Function<MentionParameter, Target> get(MentionType key){
        return registries.get(key);
    }

    private Target EVERYONE(MentionParameter parameter){
        return Target.of(manager.getPlayerList(),null);
    }

    private Target INSIDE(MentionParameter parameter){
        HashSet<ServerPlayerEntity> players = new HashSet<>(PlayerLookup.around(
                parameter.sender().getEntityWorld(),
                parameter.sender().getEntityPos(),
                Float.parseFloat(parameter.option())
        ));

        return Target.of(players,null);
    }

    private Target TEAM(MentionParameter parameter){
        Team team = scoreboard.getTeam(parameter.option());
        List<ServerPlayerEntity> players = new ArrayList<>();
        Style style = colorTeam;

        if (team != null){
            players = team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();

            if (style != null) {
                Style name = team.getFormattedName().getStyle();
                style = name.withParent(style);
            }
        }

        return Target.of(players,style);
    }

    private Target PLAYER(MentionParameter parameter){
        ServerPlayerEntity target = manager.getPlayer(parameter.option());
        Style style = colorTeam;
        HashSet<ServerPlayerEntity> players = new HashSet<>();

        if (target!=null){
            players.add(target);

            if (style != null) {
                Style name = target.getDisplayName().getStyle();
                style = name.withParent(style);
            }
        }else if(style != null){
            Integer integer = ColorUtil.getTeamColor(scoreboard, parameter.option());
            if (integer != null) style = Style.EMPTY.withColor(integer);
        }

        return new Target(players,style);
    }

    private Target LUCK_PERMS_GROUP(MentionParameter parameter){
        List<ServerPlayerEntity> players = new ArrayList<>();

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return Target.of(players,null);
        }

        players = LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayerList());

        return Target.of(players,null);
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

        return Target.of(players,null);
    }

    private Target CUSTOM(MentionParameter parameter){
        StringReader selector = new StringReader(parameter.option());
        ServerPlayerEntity player = parameter.sender();
        List<ServerPlayerEntity> players = new ArrayList<>();

        try {
            EntitySelectorReader reader = new EntitySelectorReader(selector, true);
            EntitySelector entitySelector = reader.read();
            players = entitySelector.getPlayers(player.getCommandSource());
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("Invalid selector: " + selector);
        }

        return Target.of(players, null);
    }
}
