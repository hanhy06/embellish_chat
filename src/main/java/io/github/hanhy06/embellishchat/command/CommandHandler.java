package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.CommandDispatcher;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CommandHandler implements ConfigListener {
    @Override
    public void onConfigReload(Config newConfig) {
        String alias = newConfig.command_alias();
        if (alias == null || alias.isBlank()) {
            return;
        }

        EmbellishChat.SERVER.executeIfPossible(() -> {
            CommandDispatcher<CommandSourceStack> dispatcher = EmbellishChat.SERVER.getCommands().getDispatcher();

            dispatcher.register(
                    Commands.literal(alias)
                            .redirect(dispatcher.getRoot().getChild(EmbellishChat.MOD_ID))
            );

            for (ServerPlayer player : EmbellishChat.SERVER.getPlayerList().getPlayers()) {
                EmbellishChat.SERVER.getCommands().sendCommands(player);
            }
        });
    }

    public static void registerCommand(){
        AdminCommand.registerCommand();
        UserCommand.registerCommand();
    }
}
