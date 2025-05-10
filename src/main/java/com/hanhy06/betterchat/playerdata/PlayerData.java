package com.hanhy06.betterchat.playerdata;

import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    private final String playerName;
    private final UUID playerUUID;
    private boolean notificationsEnabled;
    private int teamColor;
    private int lastPage;

    public PlayerData(String name, UUID uuid, boolean notificationsEnabled,int teamColor, int lastPage){
        this.playerName = name;
        this.playerUUID = uuid;
        this.notificationsEnabled = notificationsEnabled;
        this.teamColor = teamColor;
        this.lastPage = lastPage;
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

    public int getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(int teamColor) {
        this.teamColor = teamColor;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }
}
