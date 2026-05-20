package io.github.hanhy06.embellishchat.suggestion;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionService implements ConfigListener {
    private static final Map<String, List<String>> CANDIDATES_BY_PERMISSION = new HashMap<>();
    private static final Map<String, Boolean> PLAYER_HINT_BY_PERMISSION = new HashMap<>();

    public static void registerPayload() {
        PayloadTypeRegistry.clientboundPlay().register(SuggestionCandidatePayload.TYPE, SuggestionCandidatePayload.CODEC);

        ServerPlayerEvents.JOIN.register(player -> {
            if (ServerPlayNetworking.canSend(player, SuggestionCandidatePayload.TYPE)) {
                ServerPlayNetworking.send(player, createCandidates(player));
            }
        });
    }

    public static SuggestionCandidatePayload createCandidates(ServerPlayer player) {
        List<String> candidates = new ArrayList<>();
        boolean playerSuggestion = false;

        List<String> permissions = PermissionUtil.getPermissions(player, CANDIDATES_BY_PERMISSION.keySet());
        for (String permission : permissions) {
            candidates.addAll(CANDIDATES_BY_PERMISSION.getOrDefault(permission, List.of()));
            playerSuggestion = playerSuggestion || PLAYER_HINT_BY_PERMISSION.get(permission);
        }

        return new SuggestionCandidatePayload(candidates,playerSuggestion);
    }

    @Override
    public void onConfigReload(Config newConfig) {
        CANDIDATES_BY_PERMISSION.clear();
        PLAYER_HINT_BY_PERMISSION.clear();

        for (Map.Entry<String, List<MentionRule>> entry : newConfig.mention_rules().entrySet()) {
            List<String> candidates = new ArrayList<>();
            boolean playerSuggestion = false;

            for (MentionRule rule : entry.getValue()) {
                candidates.add(rule.pattern().pattern());

                if (playerSuggestion) continue;
                for (MentionAction action : rule.mentions()) {
                    if (action.mentionType() == MentionType.PLAYER) {
                        playerSuggestion = true;
                        break;
                    }
                }
            }

            CANDIDATES_BY_PERMISSION.put(entry.getKey(), candidates);
            PLAYER_HINT_BY_PERMISSION.put(entry.getKey(), playerSuggestion);

            for (ServerPlayer player:EmbellishChat.SERVER.getPlayerList().getPlayers()){
                if (ServerPlayNetworking.canSend(player, SuggestionCandidatePayload.TYPE)) {
                    ServerPlayNetworking.send(player, createCandidates(player));
                }
            }
        }
    }
}
