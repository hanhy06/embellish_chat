package com.hanhy06.betterchat.mention;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.mention.data.MentionData;
import com.hanhy06.betterchat.util.Timestamp;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Mention {
    private final UserCache userCache;
    private final PlayerManager playerManager;
    private final PlayerDataManager playerDataManager;

    public Mention(UserCache userCache, PlayerManager playerManager) {
        this.userCache = userCache;
        this.playerManager = playerManager;
        this.playerDataManager = new PlayerDataManager(userCache, BetterChat.getModDirectoryPath());
    }

    public List<String> mention(ServerPlayerEntity sender, String originalMessage) {
        List<String> mentionedPlayerNames = new ArrayList<>();
        if (originalMessage == null || !originalMessage.contains("@")) {
            return mentionedPlayerNames;
        }

        int searchIndex = 0;

        while (true) {
            int startIndex = originalMessage.indexOf('@', searchIndex);
            if (startIndex == -1) {
                break;
            }

            String potentialName = extractPotentialName(originalMessage, startIndex);

            if (potentialName.isEmpty()) {
                searchIndex = startIndex + 1;
                continue;
            }

            int nameEndIndex = startIndex + 1 + potentialName.length();

            boolean isExplicit = checkExplicitMention(originalMessage, nameEndIndex);

            Optional<UUID> uuidOptional = resolvePlayerUuid(potentialName);

            if (uuidOptional.isPresent()) {
                recordMention(uuidOptional.get(), originalMessage);
                mentionedPlayerNames.add(potentialName);

                searchIndex = isExplicit ? nameEndIndex + 2 : nameEndIndex;
            } else {
                searchIndex = startIndex + 1;
            }
        }
        return mentionedPlayerNames;
    }

    private String extractPotentialName(String message, int startIndex) {
        StringBuilder nameBuilder = new StringBuilder();
        int currentIndex = startIndex + 1;
        int messageLength = message.length();

        while (currentIndex < messageLength && isValidUsernameChar(message.charAt(currentIndex))) {
            nameBuilder.append(message.charAt(currentIndex));
            currentIndex++;
        }
        return nameBuilder.toString();
    }

    private boolean isValidUsernameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean checkExplicitMention(String message, int nameEndIndex) {
        return message.length() > nameEndIndex + 1 &&
                message.charAt(nameEndIndex) == '\\' &&
                message.charAt(nameEndIndex + 1) == '@';
    }

    private Optional<UUID> resolvePlayerUuid(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return Optional.empty();
        }

        ServerPlayerEntity onlinePlayer = playerManager.getPlayer(playerName);
        if (onlinePlayer != null) {
            return Optional.of(onlinePlayer.getUuid());
        }

        return userCache.findByName(playerName).map(GameProfile::getId);
    }

    private void recordMention(UUID playerUuid, String originalMessage) {
        MentionData mentionData = new MentionData(playerUuid, Timestamp.timeStamp(), originalMessage);
        playerDataManager.addPlayerData(playerUuid, mentionData);
    }
}