package io.github.hanhy06.embellishchat.mention.rule;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.configs.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.styling.util.ColorUtil;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import io.github.hanhy06.embellishchat.util.NickNamesUtil;
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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;
    private final Style colorTeam;

    private final boolean isLuckPerms;
    private final boolean isNickName;

    private final EnumMap<MentionType, Function<MentionParameter, Target>> registries;

    public MentionRegistry(Config config) {
        MinecraftServer server = EmbellishChat.SERVER;
        this.manager = server.getPlayerManager();
        this.scoreboard = server.getScoreboard();

        if (config.TEAM_COLOR() != null){
            colorTeam = Style.EMPTY.withColor(config.TEAM_COLOR().getRGB());
        }else {
            colorTeam = null;
        }

        this.registries = new EnumMap<>(Map.ofEntries(
                entry(MentionType.EVERYONE,this::EVERYONE),
                entry(MentionType.INSIDE,this::INSIDE),
                entry(MentionType.TEAM,this::TEAM),
                entry(MentionType.PLAYER,this::PLAYER),
                entry(MentionType.WORLD,this::WORLD),
                entry(MentionType.LUCK_PERMS_GROUP,this::LUCK_PERMS_GROUP),
                entry(MentionType.CUSTOM,this::CUSTOM)
        ));

        this.isLuckPerms = FabricLoader.getInstance().isModLoaded("luckperms");
        this.isNickName = FabricLoader.getInstance().isModLoaded("styled-nicknames");
    }

    public Function<MentionParameter, Target> get(MentionType key){
        return registries.get(key);
    }

    private Target EVERYONE(MentionParameter parameter){
        return Target.of(manager.getPlayerList(),null);
    }

    private Target INSIDE(MentionParameter parameter){
        float round;
        try {round = Float.parseFloat(parameter.option());}
        catch (NumberFormatException e) {round = 64;}

        HashSet<ServerPlayerEntity> players = new HashSet<>(PlayerLookup.around(
                parameter.player().getEntityWorld(),
                parameter.player().getEntityPos(),
                round
        ));

        return Target.of(players,null);
    }

    private Target TEAM(MentionParameter parameter){
        HashSet<ServerPlayerEntity> players = new HashSet<>();
        Team team = scoreboard.getTeam(parameter.option());
        Style style = colorTeam;

        if (team != null){
            for (String name:team.getPlayerList()){
                ServerPlayerEntity player = manager.getPlayer(name);
                if (player != null) players.add(player);
            }

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

        if (target == null && isNickName) {
            target = NickNamesUtil.getPlayerByNickName(parameter.option());
        }

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

    private Target WORLD(MentionParameter parameter){
        String worldName = parameter.option();
        MinecraftServer server = EmbellishChat.SERVER;
        ServerWorld targetWorld = null;

        for (ServerWorld world : server.getWorlds()) {
            String id = world.getRegistryKey().getValue().toString();
            if (id.equals(worldName) || world.getRegistryKey().getValue().getPath().equals(worldName)) {
                targetWorld = world;
                break;
            }
        }

        if (targetWorld != null) {
            HashSet<ServerPlayerEntity> players = new HashSet<>(PlayerLookup.world(targetWorld));
            return Target.of(players,null);
        } else {
            EmbellishChat.LOGGER.info("World {} not found. @world mention ignored.", worldName);
            return Target.of(new HashSet<>(), null);
        }
    }

    private Target LUCK_PERMS_GROUP(MentionParameter parameter){
        if (isLuckPerms) {
            HashSet<ServerPlayerEntity> players = LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayerList());
            return Target.of(players,null);
        }else {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return Target.of(new HashSet<>(),null);
        }
    }

    private Target CUSTOM(MentionParameter parameter){
        ServerPlayerEntity sender = parameter.player();
        StringReader selector = new StringReader(parameter.option());

        try {
            EntitySelectorReader reader = new EntitySelectorReader(selector, true);
            EntitySelector entitySelector = reader.read();
            List<ServerPlayerEntity> players = entitySelector.getPlayers(sender.getCommandSource());
            return Target.of(players, null);
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("Invalid selector: {}", selector);
            return Target.of(new HashSet<>(), null);
        }
    }
}
