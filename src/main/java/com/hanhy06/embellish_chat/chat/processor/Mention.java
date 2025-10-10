package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.data.Target;
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
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Mention {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private SoundEvent mentionSound;
    private float mentionPitch;
    private String mentionMessage;
    private boolean offlineColorEnabled;
    private int defaultMentionColor;

    public Mention(PlayerManager manager,Scoreboard scoreboard){
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    public void updateConfig(Config config){
        this.mentionSound = SoundEvent.of(Identifier.tryParse(config.defaultMentionSound()));
        this.mentionPitch = config.defaultMentionPitch();
        this.mentionMessage = config.defaultMentionMessage();
        this.offlineColorEnabled = config.offlineColorEnabled();
        this.defaultMentionColor = config.defaultMentionColor();
    }

    public void broadcastMention(ServerPlayerEntity sender, List<Receiver> receivers){
        MutableText titleText = sender.getName().copy();
        titleText.styled(style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender)));
        titleText.append(
                Text.literal(mentionMessage).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );

        for (Receiver receiver : new HashSet<>(receivers)){
            ServerPlayerEntity player = receiver.player();
            if (player == null) continue;

            player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,mentionPitch);
            player.sendMessage(titleText ,true);
        }
    }

    public List<Target> parseMentions(String raw){
        Matcher matcher = MENTION_PATTERN.matcher(raw);

        List<Target> targets = new ArrayList<>();
        while (matcher.find()){
            targets.add(new Target(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return  targets;
    }

    public List<Receiver> processReceiver(ServerPlayerEntity sender, List<Target> targets){
        return targets.stream()
                .flatMap(target -> {
                    switch (target.name()) {
                        case "everyone" -> { return targetEveryone(target).stream(); }
                        case "here" -> { return targetHere(sender, target).stream(); }
                        case "team" -> { return targetTeam(sender, target).stream(); }
                        default -> { return Stream.of(targetPlayer(target)); }
                    }
                })
                .toList();
    }

    private List<Receiver> targetEveryone(Target target){
        return manager.getPlayerList().stream()
                .map(player -> new Receiver(
                        "everyone",
                        target.begin(),
                        target.end(),
                        0x0000AA, //나중에 바꿀 예정
                        player
                ))
                .toList();
    }

    private List<Receiver> targetHere(ServerPlayerEntity sender,Target target){
        Collection<ServerPlayerEntity> players = PlayerLookup.around(sender.getEntityWorld(),sender.getEntityPos(),32);

        return players.stream()
                .map(player -> new Receiver(
                        "here",
                        target.begin(),
                        target.end(),
                        0x0000AA,
                        player
                ))
                .toList();
    }

    private List<Receiver> targetTeam(ServerPlayerEntity sender,Target target){
        Team team = sender.getScoreboardTeam();
        if (team != null){
            List<ServerPlayerEntity> players = team.getPlayerList().stream().map(manager::getPlayer).filter(Objects::nonNull).toList();
            return players.stream()
                    .map(player ->new Receiver(
                            "team",
                            target.begin(),
                            target.end(),
                            0x0000AA,
                            player
                    ))
                    .toList();
        }else {
            return List.of(
                    new Receiver(
                            "team",
                            target.begin(),
                            target.end(),
                            0x0000AA,
                            null
                    )
            );
        }


    }

    private Receiver targetPlayer(Target target){
        ServerPlayerEntity player = manager.getPlayer(target.name());

        int teamColor;
        if (player != null){
            teamColor = TeamColor.getPlayerColor(player);
        }else if (offlineColorEnabled) {
            teamColor = TeamColor.getPlayerColor(scoreboard, target.name());
        } else {
            teamColor = defaultMentionColor;
        }

        return new Receiver(
                target.name(),
                target.begin(),
                target.end(),
                teamColor,
                player
        );
    }


}