package com.hanhy06.betterchat;

import com.hanhy06.betterchat.chat.ChatHandler;
import com.hanhy06.betterchat.command.BetterChatCommand;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.chat.processor.Mention;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterChat implements ModInitializer {
	public static final String MOD_ID = "betterchat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static MinecraftServer serverInstance = null;
	public static final String MOD_DIRECTORY_NAME = "better-chat";

	private static PlayerManager manager;
	private static Mention mention;
	private static ChatHandler chatHandler;

    @Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(BetterChat::handleServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(BetterChat::handleServerStop);

		BetterChatCommand.registerBetterChatCommand();
	}

	private static void handleServerStart(MinecraftServer server) {
		serverInstance = server;
        Path modDirPath = server.getSavePath(WorldSavePath.ROOT).getParent().resolve(MOD_DIRECTORY_NAME);

		if (!Files.exists(modDirPath)) {
			try {
				Files.createDirectories(modDirPath);
			} catch (IOException e) {
				LOGGER.error("Failed to create mod directory: {}. Mod may not function correctly.", modDirPath, e);
			}
		}

		manager = server.getPlayerManager();
		mention = new Mention(server.getPlayerManager(),server.getUserCache());
		chatHandler = new ChatHandler(mention);

		ConfigManager.configDataLoadedEvents(mention);
		ConfigManager.configDataLoadedEvents(chatHandler);
        ConfigManager.handleServerStart(modDirPath);

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}

	private static void handleServerStop(MinecraftServer server){
		ConfigManager.handleServerStop();
	}

	public static ChatHandler getChatHandler() {return chatHandler;}

	public static PlayerManager getManager() {
		return manager;
	}
}