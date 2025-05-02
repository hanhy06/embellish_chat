package com.hanhy06.betterchat.mention.data.playerdata;

import com.hanhy06.betterchat.mention.data.MentionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    private final String playerName;
    private final UUID playerUUID;
    private boolean notificationsEnabled;
    private List<MentionData> mentionData;

    public PlayerData(String name,UUID uuid,boolean notificationsEnabled){
        this.playerName = name;
        this.playerUUID = uuid;
        this.notificationsEnabled = notificationsEnabled;
        this.mentionData = new ArrayList<>();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public List<MentionData> getMentionData() {
        return List.copyOf(mentionData);
    }

    public void addMentionData(MentionData data){
        mentionData.add(data);
    }

    public void removeMentionData(MentionData data){
        mentionData.remove(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }
}
