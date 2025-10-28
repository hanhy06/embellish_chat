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
import net.minecraft.text.Style;
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
        Set<Mention> result = new HashSet<>();
        Set<ServerPlayerEntity> players = new HashSet<>();

        Set<ParsedMention> parsedMentions = new HashSet<>();
        for (MentionRule rule : mentionRules.get(key)){
                parsedMentions.addAll(parsedMention(rule,message)) ;
        }

        HashMap<String ,ParsedTarget> parsedTargets = new HashMap<>();
        for (ParsedMention mention : parsedMentions){
            ParsedTarget target = parsedTarget(sender,mention.rule().mentions(), mention.mention());
            parsedTargets.put(
                    mention.mention(),
                    target
            );
            players.addAll(target.players());
        }

        broadcastMentions(sender,players);
        return result;
    }

    private void broadcastMentions(ServerPlayerEntity sender, Set<ServerPlayerEntity> players) {
        MutableText title = createTitle(sender);

        for (ServerPlayerEntity player : players) {
            player.sendMessage(title, true);
            player.playSoundToPlayer(mentionSound, SoundCategory.UI, 1, config.mentionPitch());
        }
    }

    private ParsedTarget parsedTarget(ServerPlayerEntity sender, List<MentionAction> actions,String mention) {
        List<ParsedTarget> targets = new ArrayList<>();

        for (MentionAction action : actions){
            String option = action.preset();
            if (option.isBlank()) option = mention;

            ParsedTarget target = registries.get(action.mentionType()).apply(
                    MentionParameter.of(sender,option)
            );

            targets.add(target);
        }

        for (ParsedTarget target : targets){
            targets.getFirst().players().retainAll(target.players());
        }

        return targets.getFirst();
    }

    private List<ParsedMention> parsedMention(MentionRule rule, String message) {
        Matcher matcher = rule.pattern().matcher(message);
        List<ParsedMention> mentions = new ArrayList<>();

        while (matcher.find()) {
            ParsedMention mention = ParsedMention.of(
                    rule, matcher.group(1), matcher.start(), matcher.end()
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
        titleText.append(sender.getDisplayName());
        titleText.append(
                Text.literal(config.mentionTitleSuffix()).styled(
                        style -> style.withBold(false).withColor(0xFFFFFF)
                )
        );
        return titleText;
    }
}