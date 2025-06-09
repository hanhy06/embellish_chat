package com.hanhy06.betterchat;

import com.hanhy06.betterchat.chat.ChatHandler;
import com.hanhy06.betterchat.command.BetterChatCommand;
import com.hanhy06.betterchat.config.ConfigManager;
import com.hanhy06.betterchat.chat.processor.Mention;
import com.hanhy06.betterchat.data.DatabaseConnector;
import com.hanhy06.betterchat.data.repository.MentionDataRepository;
import com.hanhy06.betterchat.data.repository.PlayerDataRepository;
import com.hanhy06.betterchat.data.service.MentionDataService;
import com.hanhy06.betterchat.data.service.PlayerDataService;
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
import java.sql.Connection;

public class BetterChat implements ModInitializer {
	public static final String MOD_ID = "betterchat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static MinecraftServer serverInstance = null;
	public static final String MOD_DIRECTORY_NAME = "better-chat";

	private static Connection connection;

	private static PlayerDataRepository playerDataRepository;
	private static PlayerDataService playerDataService;

	private static MentionDataRepository mentionDataRepository;
	private static MentionDataService mentionDataService;

	private static Mention mention;
	private static ChatHandler chatHandler;

    @Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(BetterChat::handleServerStart);
		ServerLifecycleEvents.SERVER_STOPPED.register(BetterChat::handleServerStop);
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

		connection = DatabaseConnector.connect(modDirPath);

		playerDataRepository = new PlayerDataRepository(connection);
		playerDataService = new PlayerDataService(playerDataRepository);
		mentionDataRepository = new MentionDataRepository(connection);
		mentionDataService = new MentionDataService(mentionDataRepository);

		mention = new Mention(playerDataService, mentionDataService, server.getPlayerManager(),server.getUserCache());
		chatHandler = new ChatHandler(mention);

		ServerPlayConnectionEvents.JOIN.register(playerDataService::handlePlayerJoin);
		ServerPlayConnectionEvents.JOIN.register(mentionDataService::handlePlayerJoin);

		ServerPlayConnectionEvents.DISCONNECT.register(playerDataService::handlePlayerLeave);

		ConfigManager.configDataLoadedEvents(playerDataService);
		ConfigManager.configDataLoadedEvents(mentionDataService);
		ConfigManager.configDataLoadedEvents(mention);
		ConfigManager.configDataLoadedEvents(chatHandler);

        ConfigManager.handleServerStart(modDirPath);

		BetterChatCommand.registerBetterChatCommand();

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}

	private static void handleServerStop(MinecraftServer server){
		ConfigManager.handleServerStop();
		mentionDataService.handleServerStop();
		DatabaseConnector.disconnect(connection);
	}

	public static MinecraftServer getServerInstance() {
		return serverInstance;
	}

	public static ChatHandler getChatHandler() {return chatHandler;}
}