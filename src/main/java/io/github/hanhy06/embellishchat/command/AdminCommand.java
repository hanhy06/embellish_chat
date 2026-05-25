package io.github.hanhy06.embellishchat.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.command.util.StressTestService;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class AdminCommand {
    private static StressTestService testService = null;

    private static java.util.function.Predicate<CommandSourceStack> requiresGamemaster() {
        return source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS);
    }

    public static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(
                Commands.literal(EmbellishChat.MOD_ID)
                        .then(Commands.literal("reload")
                                .requires(requiresGamemaster())
                                .executes(AdminCommand::executeReloadConfig)
                        )
                        .then(Commands.literal("ban")
                                .requires(requiresGamemaster())
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes(context -> executeBanOrPardon(context, true))
                                )
                        )
                        .then(Commands.literal("pardon")
                                .requires(requiresGamemaster())
                                .then(Commands.argument("target", EntityArgument.players())
                                        .executes(context -> executeBanOrPardon(context, false))
                                )
                        )
                        .then(Commands.literal("stress_test")
                                .requires(requiresGamemaster())
                                .then(Commands.argument("ticks", IntegerArgumentType.integer())
                                        .then(Commands.argument("count", IntegerArgumentType.integer())
                                                .then(Commands.argument("text", StringArgumentType.string())
                                                        .executes(AdminCommand::executeStressTest)))
                                )
                                .then(Commands.literal("stop")
                                        .executes(source -> {
                                            if (testService == null){
                                                source.getSource().sendFailure(Component.literal("Stress test is not running."));
                                            }else {
                                                testService.completeStressTest();
                                                testService = null;
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("regex_test")
                                .requires(requiresGamemaster())
                                .then(Commands.argument("regex",StringArgumentType.string())
                                        .then(Commands.argument("text",StringArgumentType.string())
                                                .executes(AdminCommand::executeRegexTest))
                                )
                        )
                )
        );

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (testService != null && testService.executeTest()) testService = null;
        });
    }

    private static int executeReloadConfig(CommandContext<CommandSourceStack> context) {
        Component feedback;
        if (ConfigManager.INSTANCE.readConfig()) {
            feedback = Component.literal("Config reloaded successfully.");
        } else {
            feedback = Component.literal("Failed to reload config. Please check the log.");
        }
        context.getSource().sendSuccess(() -> feedback, true);
        return 1;
    }

    private static int executeBanOrPardon(CommandContext<CommandSourceStack> context, boolean isBan) {
        String action = isBan ? "banned" : "pardoned";
        Set<UUID> uuids;
        String names;

        try {
            names = EntityArgument.getPlayers(context, "target").stream()
                    .map(ServerPlayer::getName)
                    .map(Component::getString)
                    .collect(Collectors.joining(", "));
            uuids = EntityArgument.getPlayers(context, "target").stream()
                    .map(ServerPlayer::getUUID)
                    .collect(Collectors.toSet());
        } catch (CommandSyntaxException e) {
            EmbellishChat.LOGGER.error("[embellish-chat/command] Unable to perform {} due to an unknown error.", action);
            return 0;
        }

        synchronized (ConfigManager.INSTANCE.LOCK_KEY) {
            if (isBan) ConfigManager.getConfig().banned_players().addAll(uuids);
            else ConfigManager.getConfig().banned_players().removeAll(uuids);
        }
        ConfigManager.INSTANCE.saveAsync();

        String result = String.format("Player(s) %s %s.", names, action);

        context.getSource().sendSuccess(() -> Component.literal(result), true);
        return 1;
    }

    private static int executeStressTest(CommandContext<CommandSourceStack> context) {
        if (testService != null) {
            context.getSource().sendSuccess(() -> Component.literal("Stress test is already running."), false);
            return 0;
        }

        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        int count = IntegerArgumentType.getInteger(context, "count");
        String text = StringArgumentType.getString(context, "text");
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendSuccess(() -> Component.literal("Player only command."), false);
            return 0;
        }

        if (count > 5000) {
            source.sendSuccess(() -> Component.literal("Count is too large. (Max 5000)"), false);
            return 0;
        }

        if (ticks < 1) {
            source.sendSuccess(() -> Component.literal("Ticks is too small. (Min 1)"), false);
            return 0;
        }

        testService = new StressTestService(ticks,count,text,source);

        source.sendSuccess(() -> Component.literal("Starting stress test..."), true);
        return 1;
    }

    private static int executeRegexTest(CommandContext<CommandSourceStack> context){
        String text = StringArgumentType.getString(context,"text");
        String regex = StringArgumentType.getString(context,"regex");
        MutableComponent result = Component.empty();

        Matcher matcher;

        try {
            matcher = Pattern.compile(regex).matcher(text);
        }catch (PatternSyntaxException e){
            context.getSource().sendFailure(Component.literal("syntax error"));
            return 0;
        }

        if (matcher.groupCount() < 2) {
            context.getSource().sendSuccess(() -> Component.literal("Two capture groups are required."), false);
            return 0;
        }

        int lastEnd = 0;
        while (matcher.find()){
            result.append(text.substring(lastEnd,matcher.start(1)));
            result.append(Component.literal(matcher.group(1)).setStyle(Style.EMPTY.withColor(0xaaffaa)));
            result.append(text.substring(matcher.end(1),matcher.start(2)));
            result.append(Component.literal(matcher.group(2)).setStyle(Style.EMPTY.withColor(0xffaaaa)));
            lastEnd = matcher.end(2);
        }
        result.append(text.substring(lastEnd));

        context.getSource().sendSuccess(() -> result,false);
        return 1;
    }

    private static int executeBanlist(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        HashSet<UUID> bannedList = ConfigManager.getConfig().banned_players();
        GameProfileCache profileCache = EmbellishChat.SERVER.getProfileCache();

        source.sendSystemMessage(PlaceHolderUtil.parseTag(
                "<gray>-----</gray> <aqua><b>Banned Player List</b></aqua> <gray>-----</gray>"
        ));

        for (UUID uuid : bannedList) {
            if (profileCache == null) continue;
            GameProfile profile = profileCache.get(uuid).orElse(null);
            if (profile == null) continue;
            String message = "<red><b>name</b></red>: " + profile.getName();
            source.sendSystemMessage(PlaceHolderUtil.parseTag(message));
        }

        source.sendSystemMessage(PlaceHolderUtil.parseTag("<gray>------------------------------</gray>"));
        return 1;
    }

}
