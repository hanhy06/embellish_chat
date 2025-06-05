package com.hanhy06.betterchat.data.repository;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataRepository {
    private final Connection connection;

    private static final String SELECT_PLAYER_DATA_BY_UUID = """
            SELECT * FROM player_data WHERE player_uuid = ?;
            """;

    private static final String SAVE_PLAYER_DATA = """
            INSERT OR REPLACE INTO player_data (player_uuid, player_name, notifications_enabled, team_color)
            VALUES (?, ?, ?, ?);
            """;

    public PlayerDataRepository(Connection connection) {
        this.connection = connection;
    }

    public PlayerData readPlayerData(UUID uuid){
        try {
            if (uuid == null || connection == null || connection.isClosed()) return null;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for UUID: {}.", uuid, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_DATA_BY_UUID)){
            preparedStatement.setString(1,uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) return new PlayerData
                        (
                                resultSet.getString("player_name"),
                                uuid,
                                resultSet.getInt("notifications_enabled") == 1,
                                resultSet.getInt("team_color")
                        );
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select player data for player uuid: {}", uuid, e);
        }

        return null;
    }

    public void savePlayerData(PlayerData playerData){
        try {
            if (playerData == null || connection == null || connection.isClosed()) return;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for UUID: {}.", playerData.getPlayerUUID(), e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PLAYER_DATA)){
            preparedStatement.setString(1,playerData.getPlayerUUID().toString());
            preparedStatement.setString(2,playerData.getPlayerName());
            preparedStatement.setInt(3,playerData.isNotificationsEnabled() ? 1:0);
            preparedStatement.setInt(4,playerData.getTeamColor());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to save player data for player uuid: {}",playerData.getPlayerUUID(),e);
        }
    }
}
