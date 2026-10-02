package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.SQL_DB_KEY;
import static org.example.TestData.SQL_DB_URL;
import static org.example.TestData.SQL_DB_USERNAME;

public class DatabaseUtils implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(DatabaseUtils.class.getName());
    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final int QUERY_TIMEOUT_SECONDS = 5;

    private final HikariDataSource dataSource;

    public DatabaseUtils() {
        Properties properties = new PropertiesFileSetup().setProperties();

        String jdbcUrl = requireValue(properties.getProperty(SQL_DB_URL), SQL_DB_URL);
        String dbUser = requireEnvironmentVariable(SQL_DB_USERNAME);
        String dbPassword = requireEnvironmentVariable(SQL_DB_KEY);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        config.setPoolName("intellij-java-sql-pool");

        this.dataSource = new HikariDataSource(config);
    }

    public List<Map<String, Object>> executeQuery(String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return toRows(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to execute database query", e);
            throw new IllegalStateException("Database query failed", e);
        }
    }

    public List<Map<String, Object>> executePreparedQuery(String query, Object... parameters) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return toRows(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to execute prepared database query", e);
            throw new IllegalStateException("Database query failed", e);
        }
    }

    private List<Map<String, Object>> toRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metadata.getColumnLabel(i), resultSet.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    private static String requireEnvironmentVariable(String name) {
        return requireValue(System.getenv(name), name);
    }

    private static String requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required configuration is missing: " + name);
        }
        return value;
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
