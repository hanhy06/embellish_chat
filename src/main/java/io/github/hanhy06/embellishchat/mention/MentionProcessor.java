package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.mention.rule.MentionParameter;
import io.github.hanhy06.embellishchat.mention.rule.MentionRegistry;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        Set<Mention> mentions = new HashSet<>();
        for (MentionRule rule:rules){
            mentions.addAll(parseMention(text,rule,player));
        }
        mentions = parseTarget(mentions,player);

        mentionBroadcast(mentions,player);

        return new ArrayList<>(mentions);
    }

    private Set<Mention> parseTarget(Set<Mention> mentions, ServerPlayerEntity player) {
        Set<Mention> result = new HashSet<>();

        for (Mention mention : mentions) {
            List<Function<MentionParameter, Target>> functions = new ArrayList<>();
            List<String> options = mention.options();
            mention.rule().mentions().forEach(action -> functions.add(registries.get(action.mentionType())));

            HashSet<ServerPlayerEntity> target = new HashSet<>();
            Style style = Style.EMPTY;

            for (int i = 0; i < functions.size(); i++) {
                MentionParameter parameter = MentionParameter.of(player, options.get(i));
                Target targets = functions.get(i).apply(parameter);

                if (target.isEmpty()) {
                    target.addAll(targets.targets());
                    style = targets.style();
                } else {
                    target.retainAll(targets.targets());
                }
            }

            Mention newMention = new Mention(
                    mention.begin(),mention.end(),List.of(),target,style,mention.rule()
            );
            result.add(newMention);
        }

        return result;
    }

    private Set<Mention> parseMention(String text,MentionRule rule,ServerPlayerEntity player){
        Set<Mention> mentions = new HashSet<>();
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

    private void mentionBroadcast(Set<Mention> mentions,ServerPlayerEntity player){
        for (Mention mention:mentions){
            Text title = PlaceHolderUtil.getParedOption(mention.rule().title(),player);
            SoundEvent sound = SoundEvent.of(mention.rule().sound());
            float pitch = mention.rule().pitch();

            mention.targets().forEach(target ->{
                target.sendMessage(title,true);
                target.playSoundToPlayer(sound, SoundCategory.UI,1,pitch);
            });
        }
    }
}
