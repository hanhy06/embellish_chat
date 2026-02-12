package io.github.hanhy06.embellishchat.command;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class EmbellishChatCommandManager implements ConfigListener {
    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.commandAlias();
        if (alias == null || alias.isBlank()) return;

        AdminCommand.registerCommand(alias);
        UserCommand.registerCommand(alias);

        ServerCommandSource source = EmbellishChat.SERVER.getCommandSource();
        ReloadCommand.tryReloadDataPacks(List.of(),source);
    }

    public static void registerCommand(){
        String defaultCommand = EmbellishChat.MOD_ID;
        AdminCommand.registerCommand(defaultCommand);
        UserCommand.registerCommand(defaultCommand);
    }
}
