package io.github.hanhy06.embellishchat.command;

import com.mojang.brigadier.context.CommandContext;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.mention.rule.MentionAction;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.mention.rule.MentionType;
import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.styling.rule.StyleType;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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

        player.sendMessage(Text.literal("--- Available Mentions ---"));
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
                    "Pattern: %s\nTargets: %s\nStyles: %s\n",
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

        player.sendMessage(Text.literal("--- Available Styles ---"));
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
                    "Pattern: %s\nStyles: %s\nPreset Options: %s\n",
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

        if (!ConfigManager.getConfig().notificationCommandEnable()) {
            player.sendMessage(Text.literal("Notification command is disabled."));
            return 1;
        }

        HashSet<UUID> players = ConfigManager.getConfig().notificationOffPlayerList();
        UUID uuid = player.getUuid();

        boolean notification = players.contains(uuid);
        if (notification){
            players.remove(uuid);
        }else {
            players.add(uuid);
        }

        String result = String.format("Mention notifications set to: %s", (notification ? "ON" : "OFF"));
        context.getSource().sendFeedback(() ->
                        Text.literal(result),
                false
        );

        ConfigManager.INSTANCE.writeConfig();
        return 1;
    }
}