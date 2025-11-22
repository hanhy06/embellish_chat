package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.mention.rule.*;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public List<Mention> handleMention(String text, List<String> keys, ServerPlayerEntity player){
        List<MentionRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(mentionRules.get(key)));

        List<Mention> mentions = new ArrayList<>();
        for (MentionRule rule:rules){
            mentions.addAll(parseMention(text,rule,player));
        }

        return mentions;
    }

    private List<Mention> parseTarget(List<Mention> mentions,ServerPlayerEntity player){
        for (Mention mention:mentions){
            List<Function<MentionParameter, Target>> functions = new ArrayList<>();
            mention.rule().mentions().forEach(action -> functions.add(registries.get(action.mentionType())));


        }

        return mentions;
    }

    private List<Mention> parseMention(String text,MentionRule rule,ServerPlayerEntity player){
        List<Mention> mentions = new ArrayList<>();
        Matcher matcher = rule.pattern().matcher(text);

        List<String> presets = new ArrayList<>();
        rule.mentions().forEach(action -> presets.add(action.preset()));

        while (matcher.find()){
            int begin = matcher.start();
            int end = matcher.end();
            List<String> options = OptionUtil.split(matcher.group(1),config.delimiter());
            options = OptionUtil.parseOption(options,presets,player);

            Mention mention = Mention.of(begin,end, options,rule);
            mentions.add(mention);
        }

        return mentions;
    }
}
