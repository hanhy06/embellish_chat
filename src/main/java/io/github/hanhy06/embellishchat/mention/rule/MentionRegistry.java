package io.github.hanhy06.embellishchat.mention.rule;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.styling.util.ColorUtil;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import io.github.hanhy06.embellishchat.util.NickNamesUtil;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final PlayerList manager;
    private final Scoreboard scoreboard;
    private final Style colorTeam;

    private final boolean isLuckPerms;
    private final boolean isNickName;

    private final EnumMap<MentionType, Function<MentionParameter, Target>> registries;

    public MentionRegistry(Config config) {
        MinecraftServer server = EmbellishChat.SERVER;
        this.manager = server.getPlayerList();
        this.scoreboard = server.getScoreboard();

        if (config.team_color() != null){
            colorTeam = Style.EMPTY.withColor(config.team_color().getRGB());
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
                entry(MentionType.PERMISSION,this::PERMISSION),
                entry(MentionType.CUSTOM,this::CUSTOM)
        ));

        this.isLuckPerms = FabricLoader.getInstance().isModLoaded("luckperms");
        this.isNickName = FabricLoader.getInstance().isModLoaded("styled-nicknames");
    }

    public Function<MentionParameter, Target> get(MentionType key){
        return registries.get(key);
    }

    private Target EVERYONE(MentionParameter parameter){
        return Target.of(manager.getPlayers(),null);
    }

    private Target INSIDE(MentionParameter parameter){
        float round;
        try {round = Float.parseFloat(parameter.option());}
        catch (NumberFormatException e) {round = 64;}

        HashSet<ServerPlayer> players = new HashSet<>(PlayerLookup.around(
                parameter.player().level(),
                parameter.player().position(),
                round
        ));

        return Target.of(players,null);
    }

    private Target TEAM(MentionParameter parameter){
        HashSet<ServerPlayer> players = new HashSet<>();
        PlayerTeam team = scoreboard.getPlayerTeam(parameter.option());
        Style style = colorTeam;

        if (team != null){
            for (String name:team.getPlayers()){
                ServerPlayer player = manager.getPlayerByName(name);
                if (player != null) players.add(player);
            }

            if (style != null) {
                Style name = team.getFormattedDisplayName().getStyle();
                style = name.applyTo(style);
            }
        }

        return Target.of(players,style);
    }

    private Target PLAYER(MentionParameter parameter){
        ServerPlayer target = manager.getPlayerByName(parameter.option());
        Style style = colorTeam;
        HashSet<ServerPlayer> players = new HashSet<>();

        if (target == null && isNickName) {
            target = NickNamesUtil.getPlayerByNickName(parameter.option());
        }

        if (target!=null){
            players.add(target);

            if (style != null) {
                Style name = target.getDisplayName().getStyle();
                style = name.applyTo(style);
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
        ServerLevel targetWorld = null;

        for (ServerLevel world : server.getAllLevels()) {
            String id = world.dimension().identifier().toString();
            if (id.equals(worldName) || world.dimension().identifier().getPath().equals(worldName)) {
                targetWorld = world;
                break;
            }
        }

        if (targetWorld != null) {
            HashSet<ServerPlayer> players = new HashSet<>(PlayerLookup.level(targetWorld));
            return Target.of(players,null);
        } else {
            EmbellishChat.LOGGER.info("World {} not found. @world mention ignored.", worldName);
            return Target.of(new HashSet<>(), null);
        }
    }

    private Target LUCK_PERMS_GROUP(MentionParameter parameter){
        if (isLuckPerms) {
            HashSet<ServerPlayer> players = LuckPermsUtil.getGroupPlayers(parameter.option(),manager.getPlayers());
            return Target.of(players,null);
        }else {
            EmbellishChat.LOGGER.info("LuckPerms not found. @group mentions will be ignored.");
            return Target.of(new HashSet<>(),null);
        }
    }

    private Target PERMISSION(MentionParameter parameter){
        HashSet<ServerPlayer> players = new HashSet<>();

        for (ServerPlayer player:manager.getPlayers()){
            if (Permissions.check(player,parameter.option())){
                players.add(player);
            }
        }

        return Target.of(players,null);
    }

    private Target CUSTOM(MentionParameter parameter){
        ServerPlayer sender = parameter.player();
        StringReader selector = new StringReader(parameter.option());

        try {
            EntitySelectorParser reader = new EntitySelectorParser(selector, true);
            EntitySelector entitySelector = reader.parse();
            List<ServerPlayer> players = entitySelector.findPlayers(sender.createCommandSourceStack());
            return Target.of(players, null);
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("Invalid selector: {}", selector);
            return Target.of(new HashSet<>(), null);
        }
    }
}
