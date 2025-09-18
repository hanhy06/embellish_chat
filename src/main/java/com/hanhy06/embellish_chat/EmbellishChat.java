package com.hanhy06.embellish_chat;

import com.hanhy06.embellish_chat.chat.ChatHandler;
import com.hanhy06.embellish_chat.command.EmbellishChatCommand;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.chat.processor.Mention;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class EmbellishChat implements ModInitializer {
	public static final String MOD_ID = "embellish_chat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ConfigManager configManager;
	private static ChatHandler chatHandler;

    @Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(EmbellishChat::handleServerStart);

		EmbellishChatCommand.registerBetterChatCommand();
	}

	private static void handleServerStart(MinecraftServer server) {
        Path fabricConfigDirPath = FabricLoader.getInstance().getConfigDir();

        configManager = new ConfigManager(fabricConfigDirPath);

        chatHandler = new ChatHandler();
        configManager.addListener(chatHandler);

		configManager.readConfig();

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}
}