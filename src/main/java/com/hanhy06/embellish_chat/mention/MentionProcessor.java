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
    private HashMap<String,List<MentionRule>> mentionRules;
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

    public void handleMention(ServerPlayerEntity sender,String message,String key){

    }

    public List<ParsedMention> parseMentions(Pattern pattern, String message){
        return List.of();
    }

    private void broadcastMention(ServerPlayerEntity sender, List<MentionTarget> targets){
        MutableText titleText = createTitle(sender);

        for (MentionTarget target : targets){
            if (target.players() == null || target.players().isEmpty()) continue;

            for (ServerPlayerEntity player : target.players()){
                player.playSoundToPlayer(mentionSound,SoundCategory.UI,1f,config.mentionPitch());
                player.sendMessage(titleText ,true);
            }
        }
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
}