package com.hanhy06.betterchat;

import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.mention.Mention;
import com.hanhy06.betterchat.preparation.Filter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
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
	private static Filter filter;

	@Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ConfigManager.registerSaveAndLoad();

		ServerLifecycleEvents.SERVER_STARTED.register(BetterChat::handleServerStart);
	}

	private static void handleServerStart(MinecraftServer server) {
		serverInstance = server;
		modDirPath = server.getPath(MOD_DIRECTORY_NAME);

		mention = new Mention(serverInstance.getUserCache(),serverInstance.getPlayerManager());
		filter = new Filter(ConfigManager.getConfigData().textFilteringKeywordList());

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

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

	public static Path getModDirPath() {
		if (modDirPath == null) {
			LOGGER.error("FATAL: Mod directory path accessed before server started! This indicates a programming error.");
			throw new IllegalStateException("Mod directory path is not initialized yet.");
		}
		return modDirPath;
	}

	public static Mention getMention(){
		return mention;
	}

	public static Filter getFilter(){
		return filter;
	}
}