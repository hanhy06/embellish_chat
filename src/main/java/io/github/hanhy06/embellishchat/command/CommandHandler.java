package io.github.hanhy06.embellishchat.command;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReloadCommand;

import java.util.List;

public class CommandHandler implements ConfigListener {
    private static String registeredAlias;

    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.command_alias();
        if (alias == null || alias.isBlank() || alias.equals(registeredAlias)) {
            return;
        }

        registeredAlias = alias;
        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) ->
                dispatcher.register(
                        CommandManager.literal(alias)
                                .redirect(dispatcher.getRoot().getChild(EmbellishChat.MOD_ID))
                )
        );

        ReloadCommand.tryReloadDataPacks(List.of(), EmbellishChat.SERVER.getCommandSource());
    }

    public static void registerCommand(){
        AdminCommand.registerCommand();
        UserCommand.registerCommand();
    }
}
