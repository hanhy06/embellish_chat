package io.github.hanhy06.embellishchat.command;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class CommandHandler implements ConfigListener {
    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.commandAlias();
        if (alias == null || alias.isBlank()) return;

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                CommandManager.literal(alias).redirect(
                        commandDispatcher.getRoot().getChild(EmbellishChat.MOD_ID)
                )
        ));

        ServerCommandSource source = EmbellishChat.SERVER.getCommandSource();
        ReloadCommand.tryReloadDataPacks(List.of(),source);
    }

    public static void registerCommand(){
        AdminCommand.registerCommand();
        UserCommand.registerCommand();
    }
}
