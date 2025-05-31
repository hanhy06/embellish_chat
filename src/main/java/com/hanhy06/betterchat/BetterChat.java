package com.hanhy06.betterchat;

import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.chat.processor.Mention;
import com.hanhy06.betterchat.data.PlayerDataManager;
import com.hanhy06.betterchat.chat.processor.Filter;
import com.hanhy06.betterchat.chat.processor.Markdown;
import com.hanhy06.betterchat.data.storage.DatabaseManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
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
	private static Path modDirPath = null;

	private static DatabaseManager databaseManager;
	private static Mention mention;
	private static PlayerDataManager playerDataManager;
	private static Filter filter;
	private static Markdown markdown;

    @Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(BetterChat::handleServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(BetterChat::handleServerStop);
	}

	private static void handleServerStart(MinecraftServer server) {
		serverInstance = server;
		modDirPath = server.getSavePath(WorldSavePath.ROOT).getParent().resolve(MOD_DIRECTORY_NAME);

		if (!Files.exists(modDirPath)) {
			try {
				Files.createDirectories(modDirPath);
			} catch (IOException e) {
				LOGGER.error("Failed to create mod directory: {}. Mod may not function correctly.", modDirPath, e);
			}
		}

		ConfigManager.handleServerStart(modDirPath);

		databaseManager = new DatabaseManager(modDirPath);
		playerDataManager = new PlayerDataManager(databaseManager);
		mention = new Mention(playerDataManager, server.getPlayerManager(),server.getUserCache());
		filter = new Filter(ConfigManager.getConfigData().textFilteringKeywordList());
		markdown = new Markdown(server.getPlayerManager());

		databaseManager.connect();

		ServerPlayConnectionEvents.JOIN.register(playerDataManager::handlePlayerJoin);
		ServerPlayConnectionEvents.DISCONNECT.register(playerDataManager::handlePlayerLeave);

		if(ConfigManager.getConfigData().saveMentionEnabled()) playerDataManager.handleServerStart();

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}

	private static void handleServerStop(MinecraftServer server){
		ConfigManager.handleServerStop();
		playerDataManager.handleServerStop();
		databaseManager.disconnect();
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

	public static DatabaseManager getDatabaseManager() {return databaseManager;}

	public static Mention getMention(){
		return mention;
	}

	public static PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public static Filter getFilter(){
		return filter;
	}

	public static Markdown getMarkdown(){
		return markdown;
	}
}