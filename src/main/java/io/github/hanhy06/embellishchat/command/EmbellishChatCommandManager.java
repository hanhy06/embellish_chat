package io.github.hanhy06.embellishchat.command;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;

public class EmbellishChatCommandManager implements ConfigListener {
    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.commandAlias();
        if (alias == null || alias.isBlank()) alias = Config.createDefault().commandAlias();

        AdminCommand.registerCommand(alias);
        UserCommand.registerCommand(alias);
    }

    public static void registerCommand(){
        String alias = Config.createDefault().commandAlias();
        AdminCommand.registerCommand(alias);
        UserCommand.registerCommand(alias);
    }
}
