package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Cooldown;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.mention.rule.MentionParameter;
import io.github.hanhy06.embellishchat.mention.rule.MentionRegistry;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final PlayerManager manager;
    private final Scoreboard scoreboard;
    private final HashSet<Cooldown> cooldowns;

    private Config config;
    private Map<String, List<MentionRule>> mentionRules;
    private MentionRegistry registries;

    private HashSet<UUID> notificationOffPlayerList;
    private boolean notification;
    private boolean broadcast;

    private int counter;

    public MentionProcessor(PlayerManager manager, Scoreboard scoreboard) {
        this.manager = manager;
        this.scoreboard = scoreboard;

        this.cooldowns = new HashSet<>();
        this.counter =0;
        ServerTickEvents.START_SERVER_TICK.register(tick ->{
            if (++counter >= 5){
                Instant now = Instant.now();
                cooldowns.removeIf(cooldown -> now.isAfter(cooldown.end()));
                counter = 0;
            }
        });
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.mentionRules = config.mentionRules();
        this.registries = new MentionRegistry(newConfig,manager, scoreboard);

        this.cooldowns.clear();
        this.notificationOffPlayerList = config.notificationOffPlayerList();
        this.notification = config.notificationCommandEnable();
        this.broadcast = config.mentionBroadcast();
    }

    public List<Mention> handleMention(String text, List<String> keys, ServerPlayerEntity player){
        List<MentionRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(mentionRules.get(key)));

        Set<Mention> mentions = new HashSet<>();
        for (MentionRule rule:rules){
            Cooldown cooldown = null;
            Set<Mention> buffer;

            if (rule.cooldown() > 0){
                cooldown = new Cooldown(player.getUuid(),Instant.now().plusSeconds(rule.cooldown()),rule);
                if (cooldowns.contains(cooldown)) continue;
            }

            buffer = parseMention(text,rule);
            if (cooldown != null && !buffer.isEmpty()) cooldowns.add(cooldown);
            mentions.addAll(buffer);
        }
        mentions = parseTarget(mentions,player);

        if(broadcast) mentionBroadcast(mentions,player);

        return new ArrayList<>(mentions);
    }

    private Set<Mention> parseTarget(Set<Mention> mentions, ServerPlayerEntity player) {
        Set<Mention> result = new HashSet<>();

        for (Mention mention : mentions) {
            List<Function<MentionParameter, Target>> functions = new ArrayList<>();
            List<String> presets = new ArrayList<>();

            mention.rule().mentions().forEach(action -> {
                functions.add(registries.get(action.mentionType()));
                presets.add(action.preset());
            });

            List<String> options = mention.options();
            options = OptionUtil.parseOption(options,presets,player);

            HashSet<ServerPlayerEntity> targets = null;
            Style style = Style.EMPTY;

            for (int i = 0; i < functions.size(); i++) {
                MentionParameter parameter = MentionParameter.of(player, options.get(i));
                Target target = functions.get(i).apply(parameter);

                if (targets == null) {
                    targets = new HashSet<>(target.targets());
                    style = target.style();
                } else {
                    targets.retainAll(target.targets());
                }
            }

            Mention newMention = new Mention(
                    mention.begin(),mention.end(),null,targets,style,mention.rule()
            );
            result.add(newMention);
        }

        return result;
    }

    private Set<Mention> parseMention(String text,MentionRule rule){
        Set<Mention> mentions = new HashSet<>();
        Matcher matcher = rule.pattern().matcher(text);

        while (matcher.find()){
            int begin = matcher.start();
            int end = matcher.end();
            List<String> options = OptionUtil.split(matcher.group(1),config.delimiter());

            Mention mention = Mention.of(begin,end, options,rule);
            mentions.add(mention);
        }

        return mentions;
    }

    private void mentionBroadcast(Set<Mention> mentions,ServerPlayerEntity player){
        for (Mention mention:mentions){
            Text title = PlaceHolderUtil.getParsedOption(mention.rule().title(),player);
            SoundEvent sound = SoundEvent.of(mention.rule().sound());
            float pitch = mention.rule().pitch();

            mention.targets().forEach(target ->{
                if (notification && notificationOffPlayerList.contains(target.getUuid())) {
                    return;
                }

                target.sendMessage(title, true);
                target.playSoundToPlayer(sound, SoundCategory.UI, 1, pitch);
            });
        }
    }
}
