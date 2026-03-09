package io.github.hanhy06.embellishchat.mention;

import io.github.hanhy06.embellishchat.config.configs.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.Cooldown;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.mention.data.Sound;
import io.github.hanhy06.embellishchat.mention.data.Target;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionParameter;
import io.github.hanhy06.embellishchat.mention.rule.MentionRegistry;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

public class MentionProcessor implements ConfigListener {
    private final HashSet<Cooldown> cooldowns;

    private Config config;
    private Map<String, List<MentionRule>> mentionRules;
    private MentionRegistry registries;

    private HashSet<UUID> notificationOffPlayerList;
    private boolean notification;
    private boolean broadcast;

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
        this.mentionRules = config.MENTION_RULES();
        this.registries = new MentionRegistry(newConfig);

        this.cooldowns.clear();
        this.notificationOffPlayerList = config.NOTIFY_OFF_PLAYERS();
        this.notification = config.NOTIFY_COMMAND_ENABLED();
        this.broadcast = config.NOTIFY_MENTION_ENABLED();
    }

    public List<Mention> handleMention(String text, List<String> keys, ServerPlayerEntity player){
        List<MentionRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(mentionRules.getOrDefault(key,List.of())));
        if (rules.isEmpty()) return List.of();

        UUID uuid = player.getUuid();
        Set<Mention> mentions = new HashSet<>();
        for (MentionRule rule:rules){
            Cooldown cooldown = null;
            Set<Mention> buffer;

            if (rule.cooldown() > 0){
                cooldown = new Cooldown(uuid,Instant.now().plusSeconds(rule.cooldown()),rule);
                if (cooldowns.contains(cooldown)) continue;
            }

            buffer = parseMention(text,rule,player);
            if (cooldown != null && !buffer.isEmpty()) cooldowns.add(cooldown);
            mentions.addAll(buffer);
        }

        mentionBroadcast(mentions,player);

        return new ArrayList<>(mentions);
    }

    private Set<Mention> parseMention(String text,MentionRule rule,ServerPlayerEntity player){
        Set<Mention> mentions = new HashSet<>();
        Matcher matcher = rule.pattern().matcher(text);

        while (matcher.find()){
            int begin = matcher.start();
            int end = matcher.end();
            List<String> options = OptionUtil.split(matcher.group(1),config.DELIMITER());

            Mention mention = Mention.of(begin,end, options,rule);
            mentions.add(parseTarget(mention,player));
        }

        return mentions;
    }


    private Mention parseTarget(Mention mention,ServerPlayerEntity player){
        List<MentionAction> actions = mention.rule().mentions();
        List<String> options = mention.options();

        HashSet<ServerPlayerEntity> targets = null;
        Style style = Style.EMPTY;

        for (int i=0;i<actions.size();i++){
            MentionAction action = actions.get(i);

            Function<MentionParameter,Target> function = registries.get(action.mentionType());
            String option = OptionUtil.selectOption(action.preset(),options.size() > i ? options.get(i):"");

            Target target = function.apply(MentionParameter.of(option,player));

            if (targets == null) {
                targets = target.targets();
                style = target.style();
            } else {
                targets.retainAll(target.targets());
            }
        }

        return Mention.of(mention.begin(),mention.end(),targets,style,mention.rule());
    }

    private void mentionBroadcast(Set<Mention> mentions,ServerPlayerEntity player){
        if(!broadcast) return;

        for (Mention mention:mentions){
            Text title = PlaceHolderUtil.parseText(mention.rule().title(),player);
            Sound sound = mention.rule().sound();

            mention.targets().forEach(target ->{
                if (notification && notificationOffPlayerList.contains(target.getUuid())) {
                    return;
                }

                if (sound != null) sound.playSoundToPlayer(target);
                target.sendMessage(title, true);
            });
        }
    }

    public void targetBroadcast(List<Mention> mentions, SignedMessage message,ServerPlayerEntity player){
        boolean onlyTarget = false;
        HashSet<ServerPlayerEntity> targets = new HashSet<>();

        for (Mention mention:mentions){
            if (mention.rule().onlyTarget()){
                onlyTarget = true;
                targets.addAll(mention.targets());
            }
        }

        if (!onlyTarget) return;

        SentMessage sentMessage = SentMessage.of(message);
        MessageType.Parameters parameters = MessageType.params(MessageType.CHAT, player);
        targets.forEach(target ->
                target.sendChatMessage(sentMessage,false, parameters)
        );

        throw new MessageProcessor.MessageBlockedException();
    }
}
