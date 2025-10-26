package com.hanhy06.embellish_chat.mention;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.rule.MentionParameter;
import com.hanhy06.embellish_chat.mention.rule.MentionRegistry;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.mention.rule.MentionType;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionProcessor implements ConfigListener {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    private final StylingProcessor styler;
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

    public MentionProcessor(StylingProcessor styler,PlayerManager manager, Scoreboard scoreboard) {
        this.styler = styler;
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    public List<MentionTarget> handleMention(ServerPlayerEntity sender,String message,String key){
        List<MentionRule> rules = mentionRules.get(key);
        List<MentionTarget> targets = new ArrayList<>();

        for (MentionRule rule : rules){
            List<ParsedMention> mentions = parseMentions(rule.pattern(),message);
            targets.addAll(
                    identifyTargets(sender,mentions,rule)
            );
        }

        return targets;
    }

    private List<MentionTarget> identifyTargets(ServerPlayerEntity sender,List<ParsedMention> mentions,MentionRule rule){
        List<MentionTarget> targets = new ArrayList<>();

        for (ParsedMention mention : mentions){
            List<ServerPlayerEntity> players = identifyPlayers(sender,mention.mention(),rule.mentionType());
            MutableText text = styler.applyStyles(Text.of(mention.mention()).copy(),rule.actions());
            targets.add(
                    new MentionTarget(
                            mention.begin(),
                            mention.end(),
                            text,
                            players
                    )
            );
        }

        return targets;
    }

    private List<ServerPlayerEntity> identifyPlayers(ServerPlayerEntity sender,String mention, MentionType mentionType){
        Function<MentionParameter,List<ServerPlayerEntity>> function = registries.get(mentionType);
        return function.apply(MentionParameter.of(sender,mention));
    }

    private List<ParsedMention> parseMentions(Pattern pattern, String message){
        Matcher matcher = pattern.matcher(message);
        List<ParsedMention> mentions = new ArrayList<>();

        while(matcher.find()) {
            mentions.add(
                    new ParsedMention(
                            matcher.group(),
                            matcher.start(),
                            matcher.end()
                    )
            );
        }

        return mentions;
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