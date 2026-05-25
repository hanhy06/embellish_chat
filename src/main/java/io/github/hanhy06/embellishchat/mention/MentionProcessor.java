package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Cooldown;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionParameter;
import io.github.hanhy06.embellishchat.mention.rule.MentionRegistry;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.util.MessageBlockedException;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final HashSet<Cooldown> cooldowns;

    private Config config;
    private Map<String, List<MentionRule>> mentionRules;
    private MentionRegistry registry;

    private HashSet<UUID> notifyOffPlayers;
    private boolean notifyCommandEnabled;
    private boolean notifyMentionEnabled;

    private int counter;

    public MentionProcessor() {
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
        this.mentionRules = config.mention_rules();
        this.registry = new MentionRegistry(newConfig);

        this.cooldowns.clear();
        this.notifyOffPlayers = config.notify_off_players();
        this.notifyCommandEnabled = config.notify_command_enabled();
        this.notifyMentionEnabled = config.notify_mention_enabled();
    }

    public List<Mention> handleMention(String text, List<String> keys, ServerPlayer player){
        List<MentionRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(mentionRules.getOrDefault(key,List.of())));
        if (rules.isEmpty()) return List.of();

        UUID uuid = player.getUUID();
        Set<Mention> mentions = new HashSet<>();
        Set<Integer> mentionBegins = new HashSet<>();
        for (MentionRule rule:rules){
            Matcher matcher = rule.pattern().matcher(text);
            if (!matcher.find()) continue;

            Cooldown cooldown = null;
            Set<Mention> buffer;
            if (rule.cooldown() > 0){
                cooldown = new Cooldown(uuid,Instant.now().plusSeconds(rule.cooldown()),rule);
                if (cooldowns.contains(cooldown)) throw new MessageBlockedException("You are still on cooldown.");
            }

            buffer = parseMention(matcher,rule,player,mentionBegins);
            if (cooldown != null && !buffer.isEmpty()) cooldowns.add(cooldown);
            mentions.addAll(buffer);
        }

        mentionBroadcast(mentions,player);

        return new ArrayList<>(mentions);
    }

    private Set<Mention> parseMention(Matcher matcher,MentionRule rule,ServerPlayer player,Set<Integer> mentionBegins){
        Set<Mention> mentions = new HashSet<>();

        do {
            int begin = matcher.start();
            if (!mentionBegins.add(begin)) continue;

            int end = matcher.end();
            List<String> options = OptionUtil.split(matcher.group(1),config.delimiter());

            Mention mention = Mention.of(begin,end, options,rule);
            mentions.add(parseTarget(mention,player));
        } while (matcher.find());

        return mentions;
    }


    private Mention parseTarget(Mention mention,ServerPlayer player){
        List<MentionAction> actions = mention.rule().mentions();
        List<String> options = mention.options();

        HashSet<ServerPlayer> targets = null;
        Style style = Style.EMPTY;

        for (int i=0;i<actions.size();i++){
            MentionAction action = actions.get(i);
            String option = OptionUtil.selectOption(action.preset(),options,i);
            Target target = registry.apply(action.mentionType(),MentionParameter.of(option,player));

            if (targets == null) {
                targets = target.targets();
                style = target.style();
            } else {
                targets.retainAll(target.targets());
            }
        }

        return mention.with(targets,style);
    }

    private void mentionBroadcast(Set<Mention> mentions,ServerPlayer player){
        if(!notifyMentionEnabled) return;

        for (Mention mention:mentions){
            Component title = PlaceHolderUtil.parseText(mention.rule().title(),player);
            Sound sound = mention.rule().sound();

            mention.targets().forEach(target ->{
                if (notifyCommandEnabled && notifyOffPlayers.contains(target.getUUID())) {
                    return;
                }

                if (sound != null) sound.playSoundToPlayer(target);
                target.displayClientMessage(title, true);
            });
        }
    }

    public void targetBroadcast(List<Mention> mentions, PlayerChatMessage message,ServerPlayer player){
        boolean onlyTarget = false;
        HashSet<ServerPlayer> targets = new HashSet<>();

        for (Mention mention:mentions){
            if (mention.rule().onlyTarget()){
                onlyTarget = true;
                targets.addAll(mention.targets());
            }
        }

        if (!onlyTarget) return;

        OutgoingChatMessage sentMessage = OutgoingChatMessage.create(message);
        ChatType.Bound parameters = ChatType.bind(ChatType.CHAT, player);
        targets.forEach(target ->
                target.sendChatMessage(sentMessage,false, parameters)
        );

        throw new MessageBlockedException();
    }
}
