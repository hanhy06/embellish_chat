package com.hanhy06.embellish_chat.message;

import com.hanhy06.embellish_chat.config.Config;
import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.mention.MentionProcessor;
import com.hanhy06.embellish_chat.mention.data.Mention;
import com.hanhy06.embellish_chat.styling.StylingProcessor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageProcessor implements ConfigListener {
    public static MessageProcessor INSTANCE;

    private final MentionProcessor mentionProcessor;
    private final StylingProcessor stylingManager;
    private final PlayerManager playerManager;

    private Config config;
    private List<UUID> bannedPlayerList;

    public MessageProcessor(MentionProcessor mentionProcessor, StylingProcessor stylingManager,PlayerManager playerManager) {
        INSTANCE = this;
        this.mentionProcessor = mentionProcessor;
        this.stylingManager = stylingManager;
        this.playerManager = playerManager;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.bannedPlayerList = config.bannedPlayerList();
    }

    public SignedMessage handleMessage(SignedMessage message) {
        if (bannedPlayerList.contains(message.getSender())) return message;

        MutableText textMessage = message.getContent().copy();
        String stringMessage = message.getContent().getString();

        ServerPlayerEntity sender = playerManager.getPlayer(message.getSender());
//        List<String> permissions = getPermissions(sender);

        List<Mention> mentions = mentionProcessor.handleMention(
                sender,
                stringMessage,
                "mention"
        );

        textMessage = stylingManager.applyMention(textMessage,mentions);
        textMessage = stylingManager.applyStylingRule(
                textMessage,
                "chat"
        );

//        for (String permission : permissions){
//            mentions.addAll(
//                    mentionProcessor.handleMention(
//                            sender,
//                            stringMessage,
//                            permission
//                    )
//            );
//
//            textMessage = stylingManager.applyStylingRule(
//                    textMessage,
//                    permission
//            );
//        }

        return message.withUnsignedContent(textMessage);
    }

//    private List<String> getPermissions(ServerPlayerEntity sender){
//        User user = luckPerms.getUserManager().getUser(sender.getUuid());
//        QueryOptions query = luckPerms.getContextManager().getQueryOptions(sender);
//        CachedPermissionData permission = user.getCachedData().getPermissionData(query);
//        return permission.getPermissionMap().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
//    }
}
