package com.hanhy06.betterchat.data;

import com.hanhy06.betterchat.BetterChat;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
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

    private static final String CREATE_INDEX_BY_MENTION_RECEIVER = """
            CREATE INDEX IF NOT EXISTS idx_mention_receiver
            ON mention_data(receiver_uuid);
            """;

    public static Connection connect(Path modDirPath) {
        Path modDbPath = modDirPath.resolve(MOD_DB_FILE_NAME);
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + modDbPath.toString());
            BetterChat.LOGGER.info("Successfully connected to database: {}", modDbPath);

            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_PLAYER_DATA_TABLE);
                statement.execute(CREATE_MENTION_DATA_TABLE);
                statement.execute(CREATE_INDEX_BY_MENTION_RECEIVER);
                statement.execute("PRAGMA foreign_keys = ON;");
                BetterChat.LOGGER.info("Database tables created or already exist.");
            }

        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to initialize database.", e);
        }

        return connection;
    }

    public static void disconnect(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                BetterChat.LOGGER.info("Database connection closed.");
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to close database connection.", e);
        }
    }
}
