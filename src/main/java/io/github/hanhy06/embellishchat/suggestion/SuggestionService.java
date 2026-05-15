package io.github.hanhy06.embellishchat.suggestion;

import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class SuggestionService {
    public static void registerPayload(){
        PayloadTypeRegistry.clientboundPlay().register(SuggestionCandidatePayload.TYPE, SuggestionCandidatePayload.CODEC);

        ServerPlayerEvents.JOIN.register(player -> {
            if (ServerPlayNetworking.canSend(player, SuggestionCandidatePayload.TYPE)) {
                ServerPlayNetworking.send(player, createCandidates(player));
            }
        });
    }

    public static SuggestionCandidatePayload createCandidates(ServerPlayer player){
        LinkedHashMap<String,List<MentionRule>> mentionRules = ConfigManager.getConfig().mention_rules();
        List<String> patterns = new ArrayList<>();

        List<String> permissions = PermissionUtil.getPermissions(player, mentionRules.keySet());
        for (String permission:permissions){
            patterns.addAll(mentionRules.get(permission).stream()
                    .map(MentionRule::pattern)
                    .map(Pattern::pattern)
                    .toList());
        }

        return new SuggestionCandidatePayload(patterns,List.of(),false);
    }
}
