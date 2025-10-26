package com.hanhy06.embellish_chat.mention;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.rule.MentionRegistry;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionProcessor implements ConfigListener {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private Config config;
    private SoundEvent mentionSound;
    private HashMap<String,MentionRule> mentionRules;
    private MentionRegistry registries;

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.mentionSound =  SoundEvent.of(Identifier.tryParse(config.mentionSound()));
        this.mentionRules = config.mentionRules();
        this.registries = new MentionRegistry(newConfig,manager,scoreboard);
    }

    public MentionProcessor(PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    public void broadcastMention(ServerPlayerEntity sender, List<MentionTarget> targets){
        MutableText titleText = createTitle(sender);

        for (MentionTarget target : targets){
            if (target.players() == null || target.players().isEmpty()) continue;

            for (ServerPlayerEntity player : target.players()){
                player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,config.mentionPitch());
                player.sendMessage(titleText ,true);
            }
        }
    }

    public void handleMention(ServerPlayerEntity sender,String message,String key){
        MentionRule rule = mentionRules.get(key);

        List<ParsedMention> parsedMentions = parseMentions(rule.pattern(),message);
//        List<ServerPlayerEntity> targets = registries.get(rule.mentionType()).apply()
    }

    public List<ParsedMention> parseMentions(Pattern pattern, String message){
        Matcher matcher = pattern.matcher(message);

        List<ParsedMention> parsedMentions = new ArrayList<>();
        while (matcher.find()){
            parsedMentions.add(new ParsedMention(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return parsedMentions;
    }

    public List<MentionTarget> identifyMentionTargets(ServerPlayerEntity sender, List<ParsedMention> parsedMentions){
        List<MentionTarget> targets = new ArrayList<>();
        boolean canGroupMention = canUseGroupMention(sender);

        for (ParsedMention parsedMention : parsedMentions){
            String name = parsedMention.name();

            if (canGroupMention){
                switch (name) {
                    case "everyone" -> targets.add(everyone(parsedMention));
                    case "here" -> targets.add(here(sender, parsedMention));
                    case "team" -> targets.add(team(sender, parsedMention));
                    default -> targets.add(player(parsedMention));
                }
            }else {
                targets.add(player(parsedMention));
            }
        }

        return targets;
    }

    private MutableText createTitle(ServerPlayerEntity sender){
        MutableText titleText = Text.empty();
        titleText.append(
                Text.literal(config.mentionTitlePrefix()).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );
        titleText.append(
                sender.getName().copy().styled(
                        style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender,0xffffff))
                )
        );
        titleText.append(
                Text.literal(config.mentionTitleSuffix()).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );
        return titleText;
    }

    private MentionTarget everyone(ParsedMention parsedMention){
        return new MentionTarget(
                "everyone",
                parsedMention.begin(),
                parsedMention.end(),
                config.groupMentionColor(),
                manager.getPlayerList()
        );
    }

    private MentionTarget here(ServerPlayerEntity sender, ParsedMention parsedMention){
        List<ServerPlayerEntity> players = PlayerLookup.around(
                sender.getEntityWorld(),
                sender.getEntityPos(),
                config.hereRadius()
        ).stream().toList();

        return new MentionTarget(
                "here",
                parsedMention.begin(),
                parsedMention.end(),
                config.groupMentionColor(),
                players
        );
    }

    private MentionTarget team(ServerPlayerEntity sender, ParsedMention parsedMention){
        Team team = sender.getScoreboardTeam();

        if (team != null){
            int color = config.groupMentionColor();
            Formatting formatting = team.getColor();
            if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                color = formatting.getColorValue();
            }

            List<ServerPlayerEntity> players = team
                    .getPlayerList()
                    .stream()
                    .map(manager::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();

            return new MentionTarget(
                    "team",
                    parsedMention.begin(),
                    parsedMention.end(),
                    color,
                    players
            );
        }else {
            return new MentionTarget(
                    "team",
                    parsedMention.begin(),
                    parsedMention.end(),
                    config.groupMentionColor(),
                    null
            );
        }
    }

    private MentionTarget player(ParsedMention parsedMention){
        ServerPlayerEntity player = manager.getPlayer(parsedMention.name());

        Integer teamColor = null;
        if (player != null){
//            teamColor = TeamColor.getPlayerColor(player);
        }else if (config.offlineColorEnabled()) {
//            teamColor = TeamColor.getPlayerColor(scoreboard, parsedMention.name());
        } else {
            teamColor = config.mentionColor();
        }

        return new MentionTarget(
                parsedMention.name(),
                parsedMention.begin(),
                parsedMention.end(),
                teamColor,
                player != null ? List.of(player) : null
        );
    }

    private boolean canUseGroupMention(ServerPlayerEntity player){
        return !config.groupMentionOpOnly() || manager.isOperator(player.getPlayerConfigEntry());
    }
}