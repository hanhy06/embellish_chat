package com.hanhy06.embellish_chat.mention;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.mention.data.ParsedMention;
import com.hanhy06.embellish_chat.mention.data.ParsedTarget;
import com.hanhy06.embellish_chat.mention.rule.MentionParameter;
import com.hanhy06.embellish_chat.mention.rule.MentionRegistry;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.mention.rule.MentionType;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import com.hanhy06.embellish_chat.util.TeamColor;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
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
        List<MentionRule> rules = mentionRules.get(key);
        Set<ServerPlayerEntity> players = new HashSet<>();
        Set<Mention> mentions = new HashSet<>();

        for (MentionRule rule : rules) {
            List<ParsedMention> parsedMentions = parsedMentions(message, rule.pattern());
            List<ParsedTarget> parsedTargets = parsedMentions.stream()
                    .map(parsedMention -> parsedTarget(sender, parsedMention.mention(), rule.mentionType()))
                    .toList();

            players.addAll(parsedTargets.stream()
                    .flatMap(parsedTarget -> parsedTarget.players().stream())
                    .toList());

            for (int i = 0; i < parsedTargets.size(); i++) {
                ParsedMention parsedMention = parsedMentions.get(i);
                ParsedTarget parsedTarget = parsedTargets.get(i);

                MutableText text = Text.literal(parsedMention.mention()).fillStyle(parsedTarget.style());
                text = styler.applyStyles(text,rule.actions());

                Mention mention = new Mention(
                        parsedMention.begin(),
                        parsedMention.end(),
                        text
                );
                mentions.add(mention);
            }
        }

        broadcastMentions(sender, players);
        return mentions;
    }

    private void broadcastMentions(ServerPlayerEntity sender, Set<ServerPlayerEntity> players) {
        MutableText title = createTitle(sender);

        for (ServerPlayerEntity player : players) {
            player.sendMessage(title, true);
            player.playSoundToPlayer(mentionSound, SoundCategory.UI, 1, config.mentionPitch());
        }
    }

    private ParsedTarget parsedTarget(ServerPlayerEntity sender, String mention, MentionType mentionType) {
        Function<MentionParameter, ParsedTarget> function = registries.get(mentionType);
        return function.apply(MentionParameter.of(sender, mention));
    }

    private List<ParsedMention> parsedMentions(String message, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        List<ParsedMention> mentions = new ArrayList<>();

        while (matcher.find()) {
            ParsedMention mention = ParsedMention.of(
                    matcher.group(), matcher.start(), matcher.end()
            );
            mentions.add(mention);
        }

        return mentions;
    }

    private MutableText createTitle(ServerPlayerEntity sender) {
        MutableText titleText = Text.empty();
        titleText.append(
                Text.literal(config.mentionTitlePrefix()).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );
        titleText.append(
                sender.getName().copy().styled(
                        style -> style.withBold(true).withColor(TeamColor.getPlayerColor(sender, 0xffffff))
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