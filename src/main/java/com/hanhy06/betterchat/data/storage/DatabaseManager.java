package com.hanhy06.betterchat.data.storage;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.PlayerData;

import java.nio.file.Path;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private final Path modDbPath;
    private static final String MOD_DB_FILE_NAME = "better-chat.db";

    private static final String CREATE_PLAYER_DATA_TABLE = """
        CREATE TABLE IF NOT EXISTS player_data(
            player_uuid TEXT PRIMARY KEY NOT NULL,
            player_name TEXT NOT NULL,
            notifications_enabled INTEGER NOT NULL DEFAULT 1,
            team_color INTEGER NOT NULL DEFAULT 16777045
        );
        """;

    private static final String CREATE_MENTION_DATA_TABLE = """
        CREATE TABLE IF NOT EXISTS mention_data(
            mention_id INTEGER PRIMARY KEY AUTOINCREMENT,
            receiver_uuid TEXT NOT NULL,
            sender_uuid TEXT NOT NULL,
            time_stamp TEXT NOT NULL,
            message TEXT NOT NULL,
            item_data TEXT,
            is_open INTEGER NOT NULL DEFAULT 0,
            FOREIGN KEY (receiver_uuid) REFERENCES player_data (player_uuid)
        );
        """;

    private static final String SELECT_PLAYER_DATA_BY_NAME = """
            SELECT * FROM player_data WHERE player_name = ?;
            """;

    private static final String SELECT_PLAYER_DATA_BY_UUID = """
            SELECT * FROM player_data WHERE player_uuid = ?;
            """;

    private static final String SAVE_PLAYER_DATA = """
    INSERT OR REPLACE INTO player_data (player_uuid, player_name, notifications_enabled, team_color)
    VALUES (?, ?, ?, ?);
    """;

    private Connection connection = null;

    public DatabaseManager(Path modDirPath) {
        this.modDbPath = modDirPath.resolve(MOD_DB_FILE_NAME);
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + modDbPath.toString());
            BetterChat.LOGGER.info("Successfully connected to database: {}", modDbPath);

            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_PLAYER_DATA_TABLE);
                statement.execute(CREATE_MENTION_DATA_TABLE);
                BetterChat.LOGGER.info("Database tables created or already exist.");
            }

        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to initialize database.", e);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                BetterChat.LOGGER.info("Database connection closed.");
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to close database connection.", e);
        }
    }

    public PlayerData readPlayerData(String name){
        try {
            if (connection == null || connection.isClosed() || name == null || name.isBlank()) return null;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_DATA_BY_NAME)){
                preparedStatement.setString(1,name);

                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()) return new PlayerData
                            (
                                    name,
                                    UUID.fromString(resultSet.getString("player_uuid")),
                                    resultSet.getBoolean("notifications_enabled"),
                                    resultSet.getInt("team_color")
                            );
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select player data for player name: {}", name, e);
        }
        return null;
    }

    public PlayerData readPlayerData(UUID uuid){
        try {
            if (connection == null || connection.isClosed() || uuid == null) return null;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_DATA_BY_UUID)){
                preparedStatement.setString(1,uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()) return new PlayerData
                            (
                                    resultSet.getString("player_name"),
                                    uuid,
                                    resultSet.getBoolean("notifications_enabled"),
                                    resultSet.getInt("team_color")
                            );
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select player data for player uuid: {}", uuid, e);
        }
        return null;
    }

    public void savePlayerData(PlayerData playerData){
        try {
            if (connection == null || connection.isClosed() || playerData == null) return;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_PLAYER_DATA)){
                preparedStatement.setString(1,playerData.getPlayerUUID().toString());
                preparedStatement.setString(2,playerData.getPlayerName());
                preparedStatement.setInt(3,playerData.isNotificationsEnabled() ? 1:0);
                preparedStatement.setInt(4,playerData.getTeamColor());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to save player data for player uuid: {}",playerData.getPlayerUUID(),e);
        }
    }
}