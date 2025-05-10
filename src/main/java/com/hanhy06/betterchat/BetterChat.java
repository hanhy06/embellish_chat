package com.hanhy06.betterchat;

import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.mention.Mention;
import com.hanhy06.betterchat.playerdata.PlayerDataManager;
import com.hanhy06.betterchat.preparation.Filter;
import com.hanhy06.betterchat.preparation.Markdown;
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
	private static Path modDirPath = null;

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

		ConfigManager.handleServerStart(modDirPath);

		mention = new Mention(serverInstance.getUserCache(),serverInstance.getPlayerManager(),modDirPath);
		filter = new Filter(ConfigManager.getConfigData().textFilteringKeywordList());
		markdown = new Markdown(server.getPlayerManager());
		playerDataManager = new PlayerDataManager(serverInstance.getUserCache(),modDirPath);

		if (!Files.exists(modDirPath)) {
			try {
				Files.createDirectories(modDirPath);
			} catch (IOException e) {
				LOGGER.error("FATAL: Failed to create mod directory: {}. Mod may not function correctly.", modDirPath, e);
				throw new RuntimeException("Failed to create essential mod directory: " + modDirPath, e);
			}
		}

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}

	private static void handleServerStop(MinecraftServer server){
		ConfigManager.handleServerStop();
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

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