package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.mention.rule.MentionAction;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.mention.rule.MentionType;
import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import com.hanhy06.embellish_chat.styling.rule.StyleType;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;
import com.hanhy06.embellish_chat.util.PermissionUtil;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
                                    .then(
                                            CommandManager.literal("help_style")
                                                    .executes(EcCommand::executeHelpStyle)
                                    )

                    );
                }
        );
    }

    private static int executeHelpMention(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 1;

        player.sendMessage(Text.literal("_____Available mentions_____"));
        List<String> keys = PermissionUtil.getPermissionsKeys(player,"mention",ConfigManager.getConfig().mentionRules().keySet());
        List<MentionRule> rules = new ArrayList<>();

        for (String key : keys){
            rules.addAll(
                    ConfigManager.getConfig().mentionRules().get(key)
            );
        }

        for (MentionRule rule : rules){
            player.sendMessage(Text.literal(
                    String.format("use :%s",rule.pattern().pattern())
            ));
            player.sendMessage(Text.literal(
                    String.format("intersection (elements) :%s",rule
                            .mentions()
                            .stream()
                            .map(MentionAction::mentionType)
                            .map(MentionType::name)
                            .collect(Collectors.joining(", "))
                    )
            ));
            player.sendMessage(Text.literal(
                    String.format("apply styles :%s\n",rule
                            .styles().stream()
                            .map(StyleAction::styleType)
                            .map(StyleType::name)
                            .collect(Collectors.joining(", "))
                    )
            ));
        }

        return 1;
    }

    private static int executeHelpStyle(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 1;

        player.sendMessage(Text.literal("_____Available styles_____"));
        List<String> keys = PermissionUtil.getPermissionsKeys(player,"chat",ConfigManager.getConfig().stylingRules().keySet());
        List<StylingRule> rules = new ArrayList<>();

        for (String key : keys){
            rules.addAll(
                    ConfigManager.getConfig().stylingRules().get(key)
            );
        }

        for (StylingRule rule : rules){
            player.sendMessage(Text.literal(
                    String.format("use :%s",rule.pattern().pattern())
            ));
            player.sendMessage(Text.literal(
                    String.format("apply styles :%s",rule
                            .actions()
                            .stream()
                            .map(StyleAction::styleType)
                            .map(StyleType::name)
                            .collect(Collectors.joining(", "))
                    )
            ));
            player.sendMessage(Text.literal(
                    String.format("preset options :%s\n",rule.
                            actions()
                            .stream()
                            .map(StyleAction::preset)
                            .map(str -> String.format("\"%s\"",str.isBlank() ? "your input":str))
                            .collect(Collectors.joining(", "))
                    )
            ));
        }

        return 1;
    }
}
