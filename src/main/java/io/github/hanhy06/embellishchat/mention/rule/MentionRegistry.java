package io.github.hanhy06.embellishchat.mention.rule;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.util.*;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class MentionRegistry {
    private final PlayerList playerList;
    private final Scoreboard scoreboard;
    private final Style colorTeam;

    private final EnumMap<MentionType, Function<MentionParameter, Target>> registries;

    public MentionRegistry(Config config) {
        MinecraftServer server = EmbellishChat.SERVER;
        this.playerList = server.getPlayerList();
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
                entry(MentionType.ADVANCED_CHAT_CHANNEL,this::ADVANCED_CHAT_CHANNEL),
                entry(MentionType.PERMISSION,this::PERMISSION),
                entry(MentionType.CUSTOM,this::CUSTOM)
        ));
    }

    public Target apply(MentionType mentionType,MentionParameter parameter){
        Function<MentionParameter, Target> function = registries.get(mentionType);
        try {
            return function.apply(parameter);
        } catch (MessageBlockedException block){
            throw block;
        } catch (Exception e) {
            EmbellishChat.LOGGER.warn("[embellish-chat/mention] Failed to apply mention [{}] with option [{}]", mentionType, parameter.option(), e);
            return Target.of(new HashSet<>(),null);
        }
    }

    private Target EVERYONE(MentionParameter parameter){
        return Target.of(playerList.getPlayers(),null);
    }

    private Target INSIDE(MentionParameter parameter){
        float round = NumberUtils.toFloat(parameter.option(), 64);

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

        if (team == null) throw new MessageBlockedException("Team not found.");

        for (String name:team.getPlayers()){
            ServerPlayer player = playerList.getPlayerByName(name);
            if (player != null) players.add(player);
        }

        if (style != null) {
            Style name = team.getFormattedDisplayName().getStyle();
            style = name.applyTo(style);
        }

        return Target.of(players,style);
    }

    private Target PLAYER(MentionParameter parameter){
        ServerPlayer target = playerList.getPlayerByName(parameter.option());
        Style style = colorTeam;
        PlayerTeam playerTeam = null;
        HashSet<ServerPlayer> players = new HashSet<>();

        if (target == null && FabricUtil.isNickName) {
            target = NickNamesUtil.getPlayerByNickName(parameter.option());
        }

        if (target != null) {
            players.add(target);
        } else {
            playerTeam = scoreboard.getPlayersTeam(parameter.option());
            if (playerTeam == null) throw new MessageBlockedException("Player not found.");
        }

        if (style != null) {
            if (target != null) {
                Style name = target.getDisplayName().getStyle();
                style = name.applyTo(style);
            } else {
                style = playerTeam.getColor()
                        .map(teamColor -> Style.EMPTY.withColor(teamColor.rgb()))
                        .orElse(style);
            }
        }

        return Target.of(players, style);
    }


    private Target WORLD(MentionParameter parameter){
        String worldName = parameter.option();
        MinecraftServer server = EmbellishChat.SERVER;
        Identifier worldId = Identifier.tryParse(worldName);

        if (worldId == null) {
            throw new MessageBlockedException("Invalid world ID.");
        }

        ResourceKey<Level> worldKey = ResourceKey.create(Registries.DIMENSION, worldId);
        ServerLevel targetLevel = server.getLevel(worldKey);

        if (targetLevel != null) {
            HashSet<ServerPlayer> players = new HashSet<>(PlayerLookup.level(targetLevel));
            return Target.of(players,null);
        } else {
            throw new MessageBlockedException("World not found.");
        }
    }

    private Target LUCK_PERMS_GROUP(MentionParameter parameter){
        if (FabricUtil.isLuckPerms) {
            HashSet<ServerPlayer> players = LuckPermsUtil.getGroupPlayers(parameter.option(),playerList.getPlayers());
            return Target.of(players,null);
        }else {
            EmbellishChat.LOGGER.info("[embellish-chat/integration] LuckPerms not found. @group mentions will be ignored.");
            return Target.of(new HashSet<>(),null);
        }
    }

    private Target ADVANCED_CHAT_CHANNEL(MentionParameter parameter){
        if (FabricUtil.isAdvancedChat) {
            HashSet<ServerPlayer> players = AdvancedChatUtil.getChannelPlayers(parameter.option(),parameter.player(),playerList.getPlayers());
            return Target.of(players,null);
        }else {
            EmbellishChat.LOGGER.info("[embellish-chat/integration] Advanced Chat not found. @channel mentions will be ignored.");
            return Target.of(new HashSet<>(),null);
        }
    }

    private Target PERMISSION(MentionParameter parameter){
        HashSet<ServerPlayer> players = new HashSet<>();

        for (ServerPlayer player:playerList.getPlayers()){
            if (Permissions.check(player,parameter.option())){
                players.add(player);
            }
        }

        return Target.of(players,null);
    }

    private Target CUSTOM(MentionParameter parameter) {
        ServerPlayer sender = parameter.player();
        StringReader selector = new StringReader(parameter.option());
        try {
            EntitySelectorParser reader = new EntitySelectorParser(selector, true);
            EntitySelector entitySelector = reader.parse();
            List<ServerPlayer> players = entitySelector.findPlayers(sender.createCommandSourceStack());
            return Target.of(players, null);
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("[embellish-chat/mention] Invalid selector.", e);
            throw new MessageBlockedException("Invalid selector.");
        }
    }
}
