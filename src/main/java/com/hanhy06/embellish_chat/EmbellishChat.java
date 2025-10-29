package com.hanhy06.embellish_chat;

import com.hanhy06.embellish_chat.command.EmbellishChatCommand;
import com.hanhy06.embellish_chat.config.ConfigManager;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.message.MessageProcessor;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class EmbellishChat implements ModInitializer {
	public static final String MOD_ID = "embellish_chat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {
		LOGGER.info("{} initializing...", MOD_ID);

		ServerLifecycleEvents.SERVER_STARTED.register(EmbellishChat::handleServerStart);

		EmbellishChatCommand.registerEmbellishChat();
	}

	private static void handleServerStart(MinecraftServer server) {
        Path fabricConfigDirPath = FabricLoader.getInstance().getConfigDir();
        LuckPerms luckPerms = LuckPermsProvider.get();

        ConfigManager manager = new ConfigManager(fabricConfigDirPath);

        StylingProcessor styler = new StylingProcessor();
        MentionProcessor mention = new MentionProcessor(server.getPlayerManager(),server.getScoreboard());
        MessageProcessor message = new MessageProcessor(mention,styler, server.getPlayerManager(),luckPerms);

        manager.addListener(styler);
        manager.addListener(mention);
        manager.addListener(message);
		manager.readConfig();

		LOGGER.info("{} initialized successfully.", MOD_ID);
	}
}