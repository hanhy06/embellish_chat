package io.github.hanhy06.embellishchat;

import io.github.hanhy06.embellishchat.command.CommandHandler;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.screen.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.styling.StyleProcessor;
import io.github.hanhy06.embellishchat.styling.util.BubbleUtil;
import io.github.hanhy06.embellishchat.util.PermissionUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class EmbellishChat implements ModInitializer {
	public static final String MOD_ID = "embellish-chat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer SERVER;

    @Override
	public void onInitialize() {
        LOGGER.info("[{}] Initializing.", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(EmbellishChat::handleServerStart);

        PermissionUtil.registerPermissions();
        InventoryManager.registerLeaveEvent();
        BubbleUtil.registerTickEvent();

        CommandHandler.registerCommand();
	}

	private static void handleServerStart(MinecraftServer server) {
        EmbellishChat.SERVER = server;

        Path fabricConfigDirPath = FabricLoader.getInstance().getConfigDir();
        ConfigManager manager = new ConfigManager(fabricConfigDirPath);

        PlaceHolderUtil.registerPlaceholder();

        StyleProcessor style = new StyleProcessor();
        MentionProcessor mention = new MentionProcessor();
        MessageProcessor message = new MessageProcessor(mention,style,server.getPlayerList());
        CommandHandler command = new CommandHandler();

        manager.addListener(style);
        manager.addListener(mention);
        manager.addListener(message);
        manager.addListener(command);
		manager.readConfig();

		LOGGER.info("[{}] initialized successfully.", MOD_ID);
	}
}