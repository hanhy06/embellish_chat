package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;

    private Config config;
    private SoundEvent mentionSound;
    private Map<String, List<MentionRule>> mentionRules;
    private MentionRegistry registries;

    public MentionProcessor(PlayerManager manager, Scoreboard scoreboard) {
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
        List<ParsedMention> mentions = new ArrayList<>();

        while (matcher.find()) {
            List<String> options = List.of(matcher.group(1).split(config.delimiter()));
            mentions.add(ParsedMention.of(rule, options, matcher.start(), matcher.end()));
        }

        return mentions;
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
        List<String> options = mention.options();

        for (int i = 0; i < actions.size();i++){
            MentionAction action = actions.get(i);
            String option = OptionUtil.parseOption(options.get(i),action.preset(),sender);

            ParsedTarget target = registries.get(action.mentionType())
                    .apply(MentionParameter.of(mention, sender, option));
            targets.add(target);
        }

        ParsedTarget first = targets.getFirst();
        for (int i = 1; i < targets.size(); i++) {
            first.players().retainAll(targets.get(i).players());
            if (first.players().isEmpty()) {
                break;
            }
        }
        return first;
    }

    public void broadcastMentions(ServerPlayerEntity sender, Set<ServerPlayerEntity> players) {
        MutableText title = createTitle(sender);

        for (ServerPlayerEntity player : players) {
            player.sendMessage(title, true);
            player.playSoundToPlayer(mentionSound, SoundCategory.UI, 1, config.mentionPitch());
        }
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
