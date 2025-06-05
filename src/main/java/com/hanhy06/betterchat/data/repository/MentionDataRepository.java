package com.hanhy06.betterchat.data.repository;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.data.model.MentionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MentionDataRepository {
    private final Connection connection;

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

    private static final String UPDATE_MENTION_DATA_ITEM_DATA = """
            UPDATE mention_data
            SET item_data = ?
            WHERE mention_id = ?;
            """;

    private static final String WRITE_MENTION_DATA = """
            INSERT INTO mention_data (receiver_uuid, sender_uuid, time_stamp, message, item_data)
            VALUES (?,?,?,?,?);
            """;

    private static final String SELECT_MENTION_DATA_COUNT_BY_RECEIVER_UUID = """
            SELECT COUNT(*) AS mention_count
            FROM mention_data
            WHERE receiver_uuid = ?;
            """;

    private static final String SELECT_NOT_OPEN_MENTION_DATA_COUNT_BY_RECEIVER_UUID = """
            SELECT COUNT(*) AS mention_count
            FROM mention_data
            WHERE receiver_uuid = ? AND is_open = 0;
            """;

    public MentionDataRepository(Connection connection) {
        this.connection = connection;
    }

    public List<MentionData> readMentionData(UUID receiver_uuid, int itemCount, int startIndex){
        List<MentionData> mentionData = new ArrayList<>();

        try {
            if (receiver_uuid == null || connection ==null || connection.isClosed()) return mentionData;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for UUID: {}.", receiver_uuid, e);
            return mentionData;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MENTION_DATA_BY_RECEIVER_UUID)){
            preparedStatement.setString(1,receiver_uuid.toString());
            preparedStatement.setInt(2,itemCount);
            preparedStatement.setInt(3,startIndex);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    mentionData.add(
                            new MentionData(
                                    resultSet.getInt("mention_id"),
                                    receiver_uuid,
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

    public void updateMentionDataIsOpen(int mentionId){
        try {
            if (connection ==null || connection.isClosed()) return;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for updating is_open for mention id: {}.", mentionId, e);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MENTION_DATA_IS_OPEN)){
            preparedStatement.setInt(1,mentionId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to update is_open in mention_data for mention id: {}",mentionId,e);
        }
    }

    public void updateMentionItemData(int mentionId, String itemData) {
        try {
            if (connection == null || connection.isClosed()) return;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for updating item_data for mentionId: {}.", mentionId, e);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MENTION_DATA_ITEM_DATA)) {
            preparedStatement.setString(1, itemData);
            preparedStatement.setInt(2, mentionId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                BetterChat.LOGGER.debug("Successfully updated item_data for mention_id: {}.", mentionId);
            } else {
                BetterChat.LOGGER.warn("No mention found with mention_id: {} to update item_data, or item_data was the same.", mentionId);
            }

        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to update item_data in mention_data for mention_id: {}", mentionId, e);
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

    public int countMentionData(UUID uuid){
        try {
            if (uuid == null || connection == null || connection.isClosed()) return -1;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for receiver uuid: {}.", uuid, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MENTION_DATA_COUNT_BY_RECEIVER_UUID)){
            preparedStatement.setString(1,uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getInt("mention_count");
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to counting mention data for receiver uuid: {}",uuid,e);
        }

        return -1;
    }

    public int countNotOpenMentionData(UUID uuid){
        try {
            if (uuid == null || connection == null || connection.isClosed()) return -1;
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to check if database connection is closed for receiver uuid: {}.", uuid, e);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_NOT_OPEN_MENTION_DATA_COUNT_BY_RECEIVER_UUID)){
            preparedStatement.setString(1,uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getInt("mention_count");
                }
            }
        } catch (SQLException e) {
            BetterChat.LOGGER.error("Failed to counting mention data for receiver uuid: {}",uuid,e);
        }

        return -1;
    }
}
