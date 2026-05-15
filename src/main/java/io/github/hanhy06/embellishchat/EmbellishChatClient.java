package io.github.hanhy06.embellishchat;

import io.github.hanhy06.embellishchat.suggestion.SuggestionManager;
import net.fabricmc.api.ClientModInitializer;

public class EmbellishChatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SuggestionManager.registerReceive();

        EmbellishChat.LOGGER.info("[embellish-chat/lifecycle] Initializing client.");
    }
}
