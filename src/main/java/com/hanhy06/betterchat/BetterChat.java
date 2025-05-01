package com.hanhy06.betterchat;

import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.mention.Mention;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterChat implements DedicatedServerModInitializer {
	public static final String MOD_ID = "betterchat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static MinecraftServer serverInstance = null;
	public static final String MOD_DIRECTORY_NAME = "better-chat";
	private static Path modDirectoryPath = null;

	private static Mention mention;

	@Override
	public void onInitializeServer() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(BetterChat::handleServerStart);

		ConfigManager.registerSaveAndLoad();

		mention = new Mention(serverInstance.getUserCache(),serverInstance.getPlayerManager());
	}

	private static void handleServerStart(MinecraftServer server) {
		serverInstance = server;
		modDirectoryPath = server.getPath(MOD_DIRECTORY_NAME);

		if (!Files.exists(modDirectoryPath)) {
			try {
				Files.createDirectories(modDirectoryPath);
			} catch (IOException e) {
				LOGGER.error("FATAL: Failed to create mod directory: {}. Mod may not function correctly.", modDirectoryPath, e);
				throw new RuntimeException("Failed to create essential mod directory: " + modDirectoryPath, e);
			}
		}
		LOGGER.info("{} initialized successfully.", MOD_ID);
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

	public static Path getModDirectoryPath() {
		if (modDirectoryPath == null) {
			LOGGER.error("FATAL: Mod directory path accessed before server started! This indicates a programming error.");
			throw new IllegalStateException("Mod directory path is not initialized yet.");
		}
		return modDirectoryPath;
	}

	public static Mention getMention(){
		return mention;
	}
}