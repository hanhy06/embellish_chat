package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandHandler implements ConfigListener {
    private static String currentAlias;

    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.commandAlias();
        if (alias == null || alias.isBlank() || currentAlias != null && currentAlias.equals(alias)) {
            return;
        }
        currentAlias = alias;

        EmbellishChat.SERVER.executeSync(() -> {
            CommandDispatcher<ServerCommandSource> dispatcher = EmbellishChat.SERVER.getCommandManager().getDispatcher();

            dispatcher.register(
                    CommandManager.literal(alias)
                            .redirect(dispatcher.getRoot().getChild(EmbellishChat.MOD_ID))
            );

            for (ServerPlayerEntity player : EmbellishChat.SERVER.getPlayerManager().getPlayerList()) {
                EmbellishChat.SERVER.getCommandManager().sendCommandTree(player);
            }
        });
    }
    public static void registerCommand(){
        AdminCommand.registerCommand();
        UserCommand.registerCommand();
    }
}
