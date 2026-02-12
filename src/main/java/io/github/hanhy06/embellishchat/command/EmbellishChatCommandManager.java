package io.github.hanhy06.embellishchat.command;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;

public class EmbellishChatCommandManager implements ConfigListener {
    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.commandAlias();
        if (alias == null || alias.isBlank()) return;

        AdminCommand.registerCommand(alias);
        UserCommand.registerCommand(alias);
    }

    public static void registerCommand(){
        String defaultCommand = EmbellishChat.MOD_ID;
        AdminCommand.registerCommand(defaultCommand);
        UserCommand.registerCommand(defaultCommand);
    }
}
