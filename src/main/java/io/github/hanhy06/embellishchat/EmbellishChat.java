package io.github.hanhy06.embellishchat;

import io.github.hanhy06.embellishchat.command.CommandHandler;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import io.github.hanhy06.embellishchat.inventory.InventoryManager;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.styling.StylingProcessor;
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
//      TODO: 현제 단일 config 인걸 여러파일로 분할
//      TODO: 단일 파일 업데이트를 지원 해야함
//      TODO: StyleRule MentionRule 을 String이 아니라 MutableText 로 바꿔서 캐싱해야함
//      TODO: api 리스너 형태로 업그레이드

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

        StylingProcessor styler = new StylingProcessor();
        MentionProcessor mention = new MentionProcessor();
        MessageProcessor message = new MessageProcessor(mention,styler, server.getPlayerManager());
        CommandHandler command = new CommandHandler();

        manager.addListener(styler);
        manager.addListener(mention);
        manager.addListener(message);
        manager.addListener(command);
		manager.readConfig();

		LOGGER.info("[{}] initialized successfully.", MOD_ID);
	}
}