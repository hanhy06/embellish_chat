package com.hanhy06.betterchat.data.storage;

import com.google.gson.Gson;
import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.model.PlayerData;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    private static final String SELECT_MENTION_DATA_BY_RECEIVER_UUID = """
            SELECT * FROM mention_data
            WHERE receiver_uuid = ?
            ORDER BY mention_id DESC
            LIMIT ? OFFSET ?;
            """;

    private static final String SELECT_MENTION_DATA_BY_MENTION_ID = """
            SELECT * FROM mention_data
            WHERE mention_id = ?
            LIMIT 1;
            """;

    private static final String UPDATE_MENTION_DATA_IS_OPEN = """
            UPDATE mention_data
            SET is_open = 1
            WHERE mention_id = ?;
            """;

    private static final String WRITE_MENTION_DATA = """
            INSERT INTO mention_data (receiver_uuid, sender_uuid, time_stamp, message, item_data)
            VALUES (?,?,?,?,?);
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
                statement.execute("PRAGMA foreign_keys = ON;");
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
            if (name == null || name.isBlank() || connection == null || connection.isClosed()) return null;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for name: {}.", name, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PLAYER_DATA_BY_NAME)){
            preparedStatement.setString(1,name);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) return new PlayerData
                        (
                                name,
                                UUID.fromString(resultSet.getString("player_uuid")),
                                resultSet.getInt("notifications_enabled") == 1,
                                resultSet.getInt("team_color")
                        );
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select player data for player name: {}", name, e);
        }

        return null;
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

    public List<MentionData> readMentionData(UUID receiver_uuid, int start_index, int page_size){
        List<MentionData> mentionData = new ArrayList<>();

        try {
            if (receiver_uuid == null || connection ==null || connection.isClosed()) return mentionData;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for UUID: {}.", receiver_uuid, e);
            return mentionData;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MENTION_DATA_BY_RECEIVER_UUID)){
            preparedStatement.setString(1,receiver_uuid.toString());
            preparedStatement.setInt(2,page_size);
            preparedStatement.setInt(3,start_index);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    mentionData.add(
                            new MentionData(
                                    resultSet.getInt("mention_id"),
                                    UUID.fromString(resultSet.getString("receiver_uuid")),
                                    UUID.fromString(resultSet.getString("sender_uuid")),
                                    resultSet.getString("time_stamp"),
                                    resultSet.getString("message"),
                                    resultSet.getString("item_data"),
                                    resultSet.getBoolean("is_open")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select mention data for receiver uuid: {}", receiver_uuid, e);
        }

        return mentionData;
    }

    public MentionData readMentionData(int mentionId){
        try {
            if (connection ==null || connection.isClosed()) return null;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for mention id: {}.", mentionId, e);
            return null;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MENTION_DATA_BY_MENTION_ID)){
            preparedStatement.setInt(1,mentionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return new MentionData(
                            mentionId,
                            UUID.fromString(resultSet.getString("receiver_uuid")),
                            UUID.fromString(resultSet.getString("sender_uuid")),
                            resultSet.getString("time_stamp"),
                            resultSet.getString("message"),
                            resultSet.getString("item_data"),
                            resultSet.getBoolean("is_open")
                    );
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to select mention data for receiver mention id: {}", mentionId, e);
        }

        return null;
    }

    public void updateMentionData(int mentionId){
        try {
            if (connection ==null || connection.isClosed()) return;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for mention id: {}.", mentionId, e);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MENTION_DATA_IS_OPEN)){
            preparedStatement.setInt(1,mentionId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to update mention data for mention id: {}",mentionId,e);
        }
    }

    public void writeMentionData(MentionData mentionData){
        try {
            if (mentionData == null || connection == null || connection.isClosed()) return;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for receiver uuid: {}.", mentionData.receiver(), e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(WRITE_MENTION_DATA)){
            preparedStatement.setString(1,mentionData.receiver().toString());
            preparedStatement.setString(2,mentionData.sender().toString());
            preparedStatement.setString(3,mentionData.timeStamp());
            preparedStatement.setString(4,mentionData.message());
            preparedStatement.setString(5,mentionData.itemData());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to write mention data for receiver uuid: {}",mentionData.receiver(),e);
        }
    }
}