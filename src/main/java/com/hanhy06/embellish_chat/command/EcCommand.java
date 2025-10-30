package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;
import com.hanhy06.embellish_chat.util.PermissionUtil;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;

public class EcCommand {
    public static void registerEc(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("ec")
                                    .then(
                                            CommandManager.literal("help_mention")
                                                    .executes(EcCommand::executeHelpMention)
                                    )

                    );
                }
        );
    }

    private static int executeHelpMention(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 1;

        player.sendMessage(Text.literal("_____Available mentions_____"));
        HashMap<String,List<MentionRule>> rules = ConfigManager.getConfig().mentionRules();
        List<String> keys = PermissionUtil.getPermissionsKeys(player,rules.keySet());



        return 1;
    }
}
