package com.hanhy06.embellish_chat.command;

import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.mention.rule.MentionAction;
import com.hanhy06.embellish_chat.mention.rule.MentionRule;
import com.hanhy06.embellish_chat.mention.rule.MentionType;
import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import com.hanhy06.embellish_chat.styling.rule.StyleType;
import com.hanhy06.embellish_chat.styling.rule.StylingRule;
import com.hanhy06.embellish_chat.util.LuckPermsUtil;
import com.hanhy06.embellish_chat.util.PermissionUtil;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EcCommand {
    public static void registerEc() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(
                    CommandManager.literal("ec")
                            .then(CommandManager.literal("help")
                                    .then(CommandManager.literal("mention").executes(EcCommand::executeHelpMention))
                                    .then(CommandManager.literal("style").executes(EcCommand::executeHelpStyle))
                            )
                            .then(CommandManager.literal("notification")
                                    .executes(EcCommand::executeNotification)
                            )
            );
        });
    }

    private static int executeHelpMention(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendMessage(Text.literal("_____Available mentions_____"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().mentionRules().keySet());
        List<MentionRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().mentionRules().get(key));
        }

        for (MentionRule rule : rules) {
            String pattern = rule.pattern().pattern();
            String mentionTypes = rule.mentions()
                    .stream()
                    .map(MentionAction::mentionType)
                    .map(MentionType::name)
                    .collect(Collectors.joining(", "));
            String styles = rule.styles()
                    .stream()
                    .map(StyleAction::styleType)
                    .map(StyleType::name)
                    .collect(Collectors.joining(", "));

            String message = String.format(
                    """
                        use :%s
                        intersection (elements) :%s
                        preset options :%s
                    
                    """,
                    pattern, mentionTypes, styles
            );

            player.sendMessage(Text.literal(message));
        }

        return 1;
    }

    private static int executeHelpStyle(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendMessage(Text.literal("_____Available styles_____"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().stylingRules().keySet());
        List<StylingRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().stylingRules().get(key));
        }

        for (StylingRule rule : rules) {
            String pattern = rule.pattern().pattern();
            String styles = rule.styles()
                    .stream()
                    .map(StyleAction::styleType)
                    .map(StyleType::name)
                    .collect(Collectors.joining(", "));
            String presetOptions = rule.styles()
                    .stream()
                    .map(StyleAction::preset)
                    .map(str -> String.format("%s", str.isBlank() ? "user input" : str))
                    .collect(Collectors.joining(ConfigManager.getConfig().delimiter()));

            String message = String.format(
                    """
                        use :%s
                        apply styles :%s
                        preset options :%s
                    
                    """,
                    pattern, styles, presetOptions
            );

            player.sendMessage(Text.literal(message));
        }

        return 1;
    }

    private static int executeNotification(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) {
            context.getSource().sendFeedback(() ->
                            Text.literal("LuckPerms is not currently installed on this server. You cannot use the mention notification settings feature. Please contact the administrator."),
                    false
            );
            return 1;
        }

        boolean notification = !LuckPermsUtil.getNotification(player);
        LuckPermsUtil.setNotification(player, notification);
        String result = String.format("mention notification setting current status is %b",notification);
        context.getSource().sendFeedback(() ->
                        Text.literal(result),
                false
        );

        return 1;
    }
}