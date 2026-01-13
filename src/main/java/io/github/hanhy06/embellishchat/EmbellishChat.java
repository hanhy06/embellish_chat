package io.github.hanhy06.embellishchat;

import io.github.hanhy06.embellishchat.command.EcCommand;
import io.github.hanhy06.embellishchat.command.EmbellishChatCommand;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
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

    @Override
	public void onInitialize() {
        LOGGER.info("Initializing {}...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(EmbellishChat::handleServerStart);

        PermissionUtil.registerPermissions();
        InventoryManager.registryLeaveEvent();
		EmbellishChatCommand.registerEmbellishChat();
        EcCommand.registerEc();
	}

	private static void handleServerStart(MinecraftServer server) {
        Path fabricConfigDirPath = FabricLoader.getInstance().getConfigDir();
        ConfigManager manager = new ConfigManager(fabricConfigDirPath);

        PlaceHolderUtil.register();

        StylingProcessor styler = new StylingProcessor();
        MentionProcessor mention = new MentionProcessor(server.getPlayerManager(),server.getScoreboard());
        MessageProcessor message = new MessageProcessor(mention,styler, server.getPlayerManager());

        manager.addListener(styler);
        manager.addListener(mention);
        manager.addListener(message);
		manager.readConfig();

		LOGGER.info("[{}] initialized successfully.", MOD_ID.toUpperCase());
	}
}