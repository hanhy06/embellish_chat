package com.hanhy06.embellish_chat.mention;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.mention.data.ParsedMention;
import com.hanhy06.embellish_chat.mention.data.ParsedTarget;
import com.hanhy06.embellish_chat.mention.rule.*;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final StylingProcessor styler;
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private Config config;
    private SoundEvent mentionSound;
    private HashMap<String, List<MentionRule>> mentionRules;
    private MentionRegistry registries;

    public MentionProcessor(StylingProcessor styler, PlayerManager manager, Scoreboard scoreboard) {
        this.styler = styler;
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.mentionSound = SoundEvent.of(Identifier.tryParse(config.mentionSound()));
        this.mentionRules = config.mentionRules();
        this.registries = new MentionRegistry(newConfig, manager, scoreboard);
    }

    public Set<Mention> handleMention(ServerPlayerEntity sender, String message, String key) {
        Set<ServerPlayerEntity> players = new HashSet<>();
        Set<ParsedMention> parsedMentions = parseMentions(message, key);
        Map<String, ParsedTarget> parsedTargets = parseTargets(sender, parsedMentions);

        parsedTargets.values().forEach(target -> players.addAll(target.players()));
        broadcastMentions(sender, players);

        return new HashSet<>();
    }

    private Set<ParsedMention> parseMentions(String message, String key) {
        Set<ParsedMention> parsedMentions = new HashSet<>();
        List<MentionRule> rules = mentionRules.get(key);

        for (MentionRule rule : rules) {
            parsedMentions.addAll(parseMention(rule, message));
        }

        return parsedMentions;
    }

    private Map<String, ParsedTarget> parseTargets(ServerPlayerEntity sender, Set<ParsedMention> parsedMentions) {
        Map<String, ParsedTarget> parsedTargets = new HashMap<>();

        for (ParsedMention mention : parsedMentions) {
            ParsedTarget target = parseTarget(sender, mention.rule().mentions(), mention.mention());
            parsedTargets.put(mention.mention(), target);
        }

        return parsedTargets;
    }

    private void broadcastMentions(ServerPlayerEntity sender, Set<ServerPlayerEntity> players) {
        MutableText title = createTitle(sender);

        for (ServerPlayerEntity player : players) {
            player.sendMessage(title, true);
            player.playSoundToPlayer(mentionSound, SoundCategory.UI, 1, config.mentionPitch());
        }
    }

    private ParsedTarget parseTarget(ServerPlayerEntity sender, List<MentionAction> actions, String mention) {
        List<ParsedTarget> targets = new ArrayList<>();

        for (MentionAction action : actions) {
            String option = action.preset().isBlank() ? mention : action.preset();
            ParsedTarget target = registries.get(action.mentionType())
                    .apply(MentionParameter.of(sender, option));
            targets.add(target);
        }

        ParsedTarget first = targets.getFirst();
        for (ParsedTarget target : targets) {
            first.players().retainAll(target.players());
        }
        return first;
    }

    private List<ParsedMention> parseMention(MentionRule rule, String message) {
        Matcher matcher = rule.pattern().matcher(message);
        List<ParsedMention> mentions = new ArrayList<>();

        while (matcher.find()) {
            mentions.add(ParsedMention.of(rule, matcher.group(1), matcher.start(), matcher.end()));
        }

        return mentions;
    }

    private MutableText createTitle(ServerPlayerEntity sender) {
        return Text.empty()
                .append(Text.literal(config.mentionTitlePrefix())
                        .styled(style -> style.withBold(false).withColor(0xFFFFFF)))
                .append(sender.getDisplayName())
                .append(Text.literal(config.mentionTitleSuffix())
                        .styled(style -> style.withBold(false).withColor(0xFFFFFF)));
    }
}
