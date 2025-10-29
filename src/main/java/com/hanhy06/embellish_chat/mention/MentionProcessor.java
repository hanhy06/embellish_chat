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

    //TODO: 각 Parsed는 자기의 상위 데이터를 가져서 계층 구조를 명확히 할것
    //TODO: ParsedMention 이 MentionRule 를 ParsedTarget이 ParsedMention을
    //TODO: ParsedTarget.createMention을 만들어서 한번에 맨션데이터를 만들도록 할것
    //TODO: 스타일링을 온전히 MessageProcessor 에게 위임할것
    public List<Mention> handleMention(ServerPlayerEntity sender, String message, String key) {
        Set<ServerPlayerEntity> players = new HashSet<>();
        Set<ParsedMention> parsedMentions = parseMentions(message, key);
        List<ParsedTarget> parsedTargets = parseTargets(sender, parsedMentions);

        parsedTargets.forEach(target -> players.addAll(target.players()));
        broadcastMentions(sender, players);

        return parsedTargets.stream().map(ParsedTarget::createMention).toList();
    }

    private Set<ParsedMention> parseMentions(String message, String key) {
        Set<ParsedMention> parsedMentions = new HashSet<>();
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
            List<String> mention = List.of(matcher.group(1).split(config.delimiter()));
            mentions.add(ParsedMention.of(rule, mention, matcher.start(), matcher.end()));
        }

        return mentions;
    }

    private List<ParsedTarget> parseTargets(ServerPlayerEntity sender, Set<ParsedMention> parsedMentions) {
        List<ParsedTarget> parsedTargets = new ArrayList<>();

        for (ParsedMention mention : parsedMentions) {
            ParsedTarget target = parseTarget(sender, mention.rule().mentions(), mention.mentions());
            parsedTargets.add(target);
        }

        return parsedTargets;
    }

    private ParsedTarget parseTarget(ServerPlayerEntity sender, List<MentionAction> actions, List<String> mentions) {
        List<ParsedTarget> targets = new ArrayList<>();

        int index = 0;
        for (MentionAction action : actions) {
            String option = action.preset();
            if (option.isBlank() && index< mentions.size()){
                option = mentions.get(index);
            }
            ParsedTarget target = registries.get(action.mentionType())
                    .apply(MentionParameter.of(sender, option));
            targets.add(target);
            index++;
        }

        ParsedTarget first = targets.getFirst();
        for (ParsedTarget target : targets) {
            first.players().retainAll(target.players());
        }
        return first;
    }

    private void broadcastMentions(ServerPlayerEntity sender, Set<ServerPlayerEntity> players) {
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
