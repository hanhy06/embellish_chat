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
        int messageLength = originalMessage.length();

        while (true) {
            int startIndex = originalMessage.indexOf('@', searchIndex);
            if (startIndex == -1) {
                break;
            }

            StringBuilder nameBuilder = new StringBuilder();
            int currentIndex = startIndex + 1;
            while (currentIndex < messageLength) {
                char currentChar = originalMessage.charAt(currentIndex);
                if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                    nameBuilder.append(currentChar);
                    currentIndex++;
                } else {
                    break;
                }
            }

            String potentialName = nameBuilder.toString();

            if (potentialName.isEmpty()) {

                searchIndex = startIndex + 1;
                continue;
            }

            int nameEndIndex = startIndex + potentialName.length(); // 이름 마지막 문자 바로 다음 인덱스

            boolean isExplicit = false;
            if (messageLength > nameEndIndex + 1 && // 최소 \@ 두 글자 공간 확인
                    originalMessage.charAt(nameEndIndex) == '\\' &&
                    originalMessage.charAt(nameEndIndex + 1) == '@') {
                isExplicit = true;
            }

            Optional<UUID> uuidOptional = resolvePlayerUuid(potentialName);

            if (uuidOptional.isPresent()) {
                MentionData mentionData = new MentionData(uuidOptional.get(), Timestamp.timeStamp(), originalMessage);
                playerDataManager.addPlayerData(uuidOptional.get(), mentionData);

                mentionedPlayerNames.add(potentialName);

                searchIndex = isExplicit ? nameEndIndex + 2 : nameEndIndex; // "@name\@ " 또는 "@name " 다음부터 검색
            } else {
                searchIndex = startIndex + 1;
            }
        }
        return mentionedPlayerNames;
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
}