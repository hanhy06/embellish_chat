package io.github.hanhy06.embellishchat.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.mention.rule.MentionRule;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.styling.rule.StylingRule;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserCommand {
    public static void registerCommand(String command) {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                CommandManager.literal(command)
                        .then(CommandManager.literal("help")
                                .then(CommandManager.literal("mention").executes(UserCommand::executeHelpMention))
                                .then(CommandManager.literal("style").executes(UserCommand::executeHelpStyle))
                        )
                        .then(CommandManager.literal("notification")
                                .executes(UserCommand::executeNotification)
                        )
                        .then(CommandManager.literal("open")
                                .then(CommandManager.argument("uuid", UuidArgumentType.uuid())
                                        .executes((context) -> executeOpenInventory(context,false))
                                )
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes((context) -> executeOpenInventory(context,true))
                                )
                        )
                        .then(CommandManager.literal("preview")
                                .then(CommandManager.argument("message", StringArgumentType.string())
                                        .executes(UserCommand::executePreview)
                                )
                        )
        ));
    }

    private static int executeHelpMention(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendMessage(PlaceHolderUtil.parseTag("<gray>-----</gray> <aqua><b>Available Mentions</b></aqua> <gray>-----</gray>"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().mentionRules().keySet());
        List<MentionRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().mentionRules().get(key));
        }

        for (MentionRule rule : rules) {
            player.sendMessage(PlaceHolderUtil.parseTag(rule.comment()));
        }
        player.sendMessage(PlaceHolderUtil.parseTag("<gray>------------------------------<gray>"));

        return 1;
    }

    private static int executeHelpStyle(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendMessage(PlaceHolderUtil.parseTag("<gray>-----</gray> <aqua><b>Available Styles</b></aqua> <gray>-----</gray>"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().stylingRules().keySet());
        List<StylingRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().stylingRules().get(key));
        }

        for (StylingRule rule : rules) {
            player.sendMessage(PlaceHolderUtil.parseTag(rule.comment()));
        }
        player.sendMessage(PlaceHolderUtil.parseTag("<gray>----------------------------</gray>"));

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
        boolean notification;

        synchronized (ConfigManager.INSTANCE.LOCK_KEY) {
            notification = players.contains(uuid);
            if (notification) players.remove(uuid);
            else players.add(uuid);
        }

        String result = String.format("Mentions set to: %s", (notification ? "<green>ON</green>" : "<gray>OFF</gray>"));
        context.getSource().sendFeedback(() -> PlaceHolderUtil.parseTag(result), false);

        CompletableFuture.runAsync(() -> {
            try {
                ConfigManager.INSTANCE.writeConfig();
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to save config async", e);
            }
        });
        return 1;
    }

    private static int executeOpenInventory(CommandContext<ServerCommandSource> context,boolean isPlayer){
        ServerPlayerEntity player = context.getSource().getPlayer();
        GameProfile profile = null;

        try {
            if (isPlayer) {
                profile = EntityArgumentType.getPlayer(context, "player").getGameProfile();
            }
            else {
                UUID uuid = UuidArgumentType.getUuid(context, "uuid");
                GameProfileResolver resolver = EmbellishChat.SERVER.getApiServices().profileResolver();
                profile= resolver.getProfileById(uuid).orElse(new GameProfile(uuid,"None"));
            }
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("The specified player is invalid.");
            context.getSource().sendError(Text.literal("The specified player is invalid."));
            return 0;
        }

        SimpleNamedScreenHandlerFactory factory= InventoryManager.get(profile.id());

        if (player == null){
            context.getSource().sendError(Text.literal("Player not found."));
            return 0;
        }

        if (factory == null) {
            context.getSource().sendError(Text.literal("Inventory not found."));
            return 0;
        }

        player.openHandledScreen(factory);
        return 1;
    }

    private static int executePreview(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        SignedMessage message = SignedMessage.ofUnsigned(
                player.getUuid(),
                StringArgumentType.getString(context,"message")
        );
        message = MessageProcessor.INSTANCE.handleMessage(message);
        player.sendMessage(message.unsignedContent());

        return 1;
    }
}