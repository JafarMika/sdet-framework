package framework.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHelper {

    private final Connection connection;
    private Statement currentStatement;

    public DatabaseHelper() {
        this.connection = createConnection();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        closeCurrentStatement();
        currentStatement = connection.createStatement();
        return currentStatement.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public void closeConnection() throws SQLException {
        closeCurrentStatement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void closeCurrentStatement() throws SQLException {
        if (currentStatement != null) {
            currentStatement.close();
            currentStatement = null;
        }
    }

    private static Connection createConnection() {
        Properties properties = loadProperties();
        String url = requireProperty(properties, "db.url");
        String user = requireProperty(properties, "db.user");
        String password = requireProperty(properties, "db.password");

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect to database", e);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseHelper.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    private static String requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(key + " is not set in config.properties");
        }
        return value.trim();
    }

}
