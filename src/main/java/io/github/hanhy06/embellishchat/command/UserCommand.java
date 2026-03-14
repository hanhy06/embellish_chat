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
import io.github.hanhy06.embellishchat.styling.rule.StyleRule;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.SimpleMenuProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserCommand {
    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                Commands.literal(EmbellishChat.MOD_ID)
                        .then(Commands.literal("help")
                                .then(Commands.literal("mention").executes(UserCommand::executeHelpMention))
                                .then(Commands.literal("style").executes(UserCommand::executeHelpStyle))
                        )
                        .then(Commands.literal("notification")
                                .executes(UserCommand::executeNotification)
                        )
                        .then(Commands.literal("open")
                                .then(Commands.argument("uuid", UuidArgument.uuid())
                                        .executes((context) -> executeOpenInventory(context,false))
                                )
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes((context) -> executeOpenInventory(context,true))
                                )
                        )
//                        .then(CommandManager.literal("preview")
//                                .then(CommandManager.argument("message", StringArgumentType.greedyString())
//                                        .executes(UserCommand::executePreview)
//                                )
//                        )
        ));
    }

    private static int executeHelpMention(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>-----</gray> <aqua><b>Available Mentions</b></aqua> <gray>-----</gray>"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().mention_rules().keySet());
        List<MentionRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().mention_rules().get(key));
        }

        for (MentionRule rule : rules) {
            if (rule.comment() == null) continue;
            player.sendSystemMessage(PlaceHolderUtil.parseText(rule.comment(),player));
        }
        player.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>------------------------------<gray>"));

        return 1;
    }

    private static int executeHelpStyle(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        player.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>-----</gray> <aqua><b>Available Styles</b></aqua> <gray>-----</gray>"));
        List<String> keys = PermissionUtil.getPermissions(player, ConfigManager.getConfig().style_rules().keySet());
        List<StyleRule> rules = new ArrayList<>();

        for (String key : keys) {
            rules.addAll(ConfigManager.getConfig().style_rules().get(key));
        }

        for (StyleRule rule : rules) {
            if (rule.comment() == null) continue;
            player.sendSystemMessage(PlaceHolderUtil.parseText(rule.comment(),player));
        }
        player.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>----------------------------</gray>"));

        return 1;
    }

    private static int executeNotification(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 1;
        }

        if (!ConfigManager.getConfig().notify_command_enabled()) {
            player.sendSystemMessage(Component.literal("Notification command is disabled."));
            return 1;
        }

        HashSet<UUID> players = ConfigManager.getConfig().notify_off_players();
        UUID uuid = player.getUUID();
        boolean notification;

        synchronized (ConfigManager.INSTANCE.LOCK_KEY) {
            notification = players.contains(uuid);
            if (notification) players.remove(uuid);
            else players.add(uuid);
        }

        String result = String.format("Mentions set to: %s", (notification ? "<green>ON</green>" : "<gray>OFF</gray>"));
        context.getSource().sendSuccess(() -> PlaceHolderUtil.parseTag(result), false);

        CompletableFuture.runAsync(() -> {
            try {
                ConfigManager.INSTANCE.writeConfig();
            } catch (Exception e) {
                EmbellishChat.LOGGER.error("Failed to save config async", e);
            }
        });
        return 1;
    }

    private static int executeOpenInventory(CommandContext<CommandSourceStack> context,boolean isPlayer){
        ServerPlayer player = context.getSource().getPlayer();
        GameProfile profile = null;

        try {
            if (isPlayer) {
                profile = EntityArgument.getPlayer(context, "player").getGameProfile();
            }
            else {
                UUID uuid = UuidArgument.getUuid(context, "uuid");
                ProfileResolver resolver = EmbellishChat.SERVER.services().profileResolver();
                profile= resolver.fetchById(uuid).orElse(new GameProfile(uuid,"None"));
            }
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.warn("The specified player is invalid.");
            context.getSource().sendFailure(Component.literal("The specified player is invalid."));
            return 0;
        }

        SimpleMenuProvider factory= InventoryManager.get(profile.id());

        if (player == null){
            context.getSource().sendFailure(Component.literal("Player not found."));
            return 0;
        }

        if (factory == null) {
            context.getSource().sendFailure(Component.literal("Inventory not found."));
            return 0;
        }

        player.openMenu(factory);
        return 1;
    }

    private static int executePreview(CommandContext<CommandSourceStack> context){
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        PlayerChatMessage message = PlayerChatMessage.unsigned(
                player.getUUID(),
                StringArgumentType.getString(context,"message")
        );
        message = MessageProcessor.INSTANCE.handleMessage(message);
        player.sendSystemMessage(message.unsignedContent());

        return 1;
    }
}