package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.MentionTarget;
import io.github.hanhy06.embellishchat.mention.data.ParsedMention;
import io.github.hanhy06.embellishchat.mention.data.ParsedTarget;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionParameter;
import io.github.hanhy06.embellishchat.mention.rule.MentionRegistry;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.util.*;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private Config config;
    private Map<String, List<MentionRule>> mentionRules;
    private MentionRegistry registries;

    public MentionProcessor(PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.mentionRules = config.mentionRules();
        this.registries = new MentionRegistry(newConfig, manager, scoreboard);
    }

    public Set<ParsedMention> parseMentions(String message, String key) {
        Set<ParsedMention> parsedMentions = new HashSet<>();
        if (!mentionRules.containsKey(key)) return parsedMentions;

        List<MentionRule> rules = mentionRules.get(key);
        for (MentionRule rule : rules) {
            parsedMentions.addAll(parseMention(rule, message));
        }

        return parsedMentions;
    }

    private List<ParsedMention> parseMention(MentionRule rule, String message) {
        Matcher matcher = rule.pattern().matcher(message);
        List<ParsedMention> parsedMentions = new ArrayList<>();

        while (matcher.find()) {
            List<String> mentions = OptionUtil.split(matcher.group(1),config.delimiter());
            parsedMentions.add(ParsedMention.of(rule, mentions, matcher.start(), matcher.end()));
        }

        return parsedMentions;
    }

    public List<ParsedTarget> parseTargets(ServerPlayerEntity sender, Set<ParsedMention> parsedMentions) {
        List<ParsedTarget> parsedTargets = new ArrayList<>();

        for (ParsedMention mention : parsedMentions) {
            ParsedTarget target = parseTarget(sender, mention);
            parsedTargets.add(target);
        }

        return parsedTargets;
    }

    private ParsedTarget parseTarget(ServerPlayerEntity sender, ParsedMention mention) {
        List<ParsedTarget> targets = new ArrayList<>();

        List<MentionAction> actions = mention.rule().mentions();
        List<String> mentions = mention.mentions();

        for (int i = 0; i < actions.size();i++){
            MentionAction action = actions.get(i);
            String option = OptionUtil.parseOption(mentions.get(i),action.preset(),sender);

            ParsedTarget target = registries.get(action.mentionType())
                    .apply(MentionParameter.of(mention, sender, option));
            targets.add(target);
        }

        ParsedTarget first = targets.getFirst();
        for (int i = 1; i < targets.size(); i++) {
            first.targets().retainAll(targets.get(i).targets());
            if (first.targets().isEmpty()) {
                break;
            }
        }
        return first;
    }

    public void broadcastMentions(Set<MentionTarget> targets) {
        for (MentionTarget target : targets) {
            target.player().sendMessage(target.title(), true);
            target.player().playSoundToPlayer(target.sound(), SoundCategory.UI,1,target.pitch());
        }
    }
}
