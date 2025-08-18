package dev.elrol.arrow.libs;

import dev.elrol.arrow.ArrowCore;
import dev.elrol.arrow.config.ArrowConfig;
import dev.elrol.arrow.data.ArrowPlayerData;

import java.sql.*;
import java.util.UUID;

public class DatabaseUtils {

    public static Connection connection;
    public static boolean isConnectionValid = false;

    public static void startConnection() {
        ArrowConfig.DatabaseInfo info = ArrowCore.CONFIG.databaseInfo;

        if(!info.isValid()) {
            ArrowCore.LOGGER.error("Database Connection Info Not Set. Features will be limited.");
            isConnectionValid = false;
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            info.hostname + ":" + info.port + "/" + info.schema,
                    info.username,
                    info.password
            );

            isConnectionValid = true;
        } catch (SQLException e) {
            ArrowCore.LOGGER.error(e.getMessage());
            isConnectionValid = false;
        }
    }

    public static void endConnection() throws SQLException {
        if(connection == null) return;
        connection.close();
        connection = null;
    }

    public static void endConnection(Statement statement) throws SQLException {
        if(statement != null) statement.close();
        endConnection();
    }

    public static void endConnection(Statement statement, ResultSet set) throws SQLException {
        if(set != null) set.close();
        endConnection(statement);
    }

    public static void connect() {
        if(!ArrowCore.CONFIG.useDatabase) return;

        try {
            startConnection();

            ArrowCore.LOGGER.info("Database Connection Successful");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM playerdata");

            checkTables();

            while(resultSet.next()) {
                System.out.println(resultSet.getString("uuid"));
            }
            endConnection(statement, resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads player data for the UUID
     * Starts Connection on call
     * @param uuid The player's UUID
     */
    public static ArrowPlayerData loadPlayerData(UUID uuid) {
        ArrowPlayerData data = null;
        if(uuid == null) return null;
        try {
            startConnection();
            Statement statement = connection.createStatement();
            statement.execute("SELECT * FROM playerdata WHERE uuid='" + uuid + "'");
            ResultSet set = statement.getResultSet();

            if(set.next()) {
                data = ArrowCoreConstants.makeGSON().fromJson(set.getString("data"), ArrowPlayerData.class);
            } else {
                data = new ArrowPlayerData(uuid);
                savePlayerData(uuid, data);
            }

            endConnection(statement, set);
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves player data for the UUID
     * Starts Connection on call
     * @param uuid
     */
    public static void savePlayerData(UUID uuid) {
        savePlayerData(uuid, ArrowCore.INSTANCE.getPlayerDataRegistry().getPlayerData(uuid));
    }

    /**
     * Saves player data for the UUID using the data
     * Starts Connection on call
     * @param uuid
     * @param data
     */
    public static void savePlayerData(UUID uuid, ArrowPlayerData data) {
        /**
        try {
            startConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO playerdata (uuid, data) VALUES ('" + uuid.toString() + "','" + data.toJsonString() + "') ON DUPLICATE KEY UPDATE data = '" + data.toJsonString() + "'");
            statement.execute();

            endConnection(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
         **/
    }

    public static void checkTables() throws SQLException {
        makeTableIfNotExist("playerdata", "uuid VARCHAR(36), data json DEFAULT NULL");
        addColumnIfNotExist("playerdata", "uuid VARCHAR(36)");
        addColumnIfNotExist("playerdata", "data json DEFAULT NULL");
    }

    public static void addColumnIfNotExist(String tableName, String columnData) {
        try {
            Statement statement = connection.createStatement();
            statement.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnData);
            statement.close();
        } catch (SQLException ignored) {}
    }
    public static void makeTableIfNotExist(String tableName, String columnData) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnData + ")");
        statement.close();
    }
}
