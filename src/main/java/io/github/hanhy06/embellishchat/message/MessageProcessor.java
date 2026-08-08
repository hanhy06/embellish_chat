package io.github.hanhy06.embellishchat.message;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.MentionProcessor;
import io.github.hanhy06.embellishchat.mention.data.Mention;
import io.github.hanhy06.embellishchat.styling.StyleProcessor;
import io.github.hanhy06.embellishchat.util.MessageBlockedException;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.*;

import static io.github.hanhy06.embellishchat.util.PermissionUtil.getPermissions;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StyleProcessor styleProcessor;
    private final PlayerList playerList;

    private Config config;
    private Set<UUID> bannedPlayerList;
    private LinkedHashMap<String,MutableComponent> messageHeader;

    public MessageProcessor(MentionProcessor mentionProcessor, StyleProcessor styleProcessor, PlayerList playerList) {
        INSTANCE = this;
        this.mentionProcessor = mentionProcessor;
        this.styleProcessor = styleProcessor;
        this.playerList = playerList;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.bannedPlayerList = config.banned_players();
        this.messageHeader = config.message_header();
    }

    public PlayerChatMessage handleMessage(PlayerChatMessage message) {
        ServerPlayer sender = playerList.getPlayer(message.sender());
        if (sender == null) return message;

        MutableComponent textMessage = message.decoratedContent().copy();
        List<String> headerKeys = getPermissions(sender, messageHeader.keySet());
        MutableComponent header = headerKeys.isEmpty() ? Component.empty() : this.messageHeader.get(headerKeys.getLast());
        header = PlaceHolderUtil.parsePlaceholder(header,sender);

        if (bannedPlayerList.contains(message.sender())) {
            return message.withUnsignedContent(header.append(textMessage));
        }

        String stringMessage = message.decoratedContent().getString();
        PlayerChatMessage result;

        try {
            PlaceHolderUtil.put(message.sender(),stringMessage);
            List<Mention> mentions = mentionProcessor.handleMention(stringMessage,getPermissions(sender, config.mention_rules().keySet()),sender);
            mentions.sort(Comparator.comparing(Mention::begin));

            textMessage = styleProcessor.applyMention(textMessage,mentions,sender);
            textMessage = styleProcessor.handleStyle(textMessage,getPermissions(sender, config.style_rules().keySet()),sender);
            textMessage = header.append(textMessage);

            result = message.withUnsignedContent(textMessage);

            mentionProcessor.targetBroadcast(mentions,result,sender);
        } catch (MessageBlockedException block){
            String blockMessage = block.getMessage();

            if (blockMessage != null && !blockMessage.isBlank()) {
                sender.sendSystemMessage(Component.literal(blockMessage).withColor(0xff0000));
            }

            return null;
        } catch (Exception e){
            EmbellishChat.LOGGER.warn("[embellish-chat/message] Failed to apply message [{}]", message, e);
            return message;
        } finally {
            PlaceHolderUtil.remove(message.sender());
        }

        return result;
    }
}
