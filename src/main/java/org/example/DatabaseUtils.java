package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.example.TestData.SQL_DB_KEY;
import static org.example.TestData.SQL_DB_URL;
import static org.example.TestData.SQL_DB_USERNAME;

public class DatabaseUtils implements AutoCloseable {

    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final int VALIDATION_TIMEOUT_MS = 5_000;
    private static final int QUERY_TIMEOUT_SECONDS = 5;
    private static final int MAX_POOL_SIZE = 5;

    private final HikariDataSource dataSource;

    public DatabaseUtils() {
        Properties properties = new PropertiesFileSetup().setProperties();

        String configuredUrl = System.getenv("SQL_DB_URL");
        if (configuredUrl == null || configuredUrl.isBlank()) {
            configuredUrl = properties.getProperty(SQL_DB_URL);
        }

        String jdbcUrl = secureJdbcUrl(requireValue(configuredUrl, "SQL_DB_URL"));
        String dbUser = requireEnvironmentVariable(SQL_DB_USERNAME);
        String dbPassword = requireEnvironmentVariable(SQL_DB_KEY);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

        // This utility is intentionally read-only. Use a separate, least-privilege
        // write path if the application later needs INSERT/UPDATE/DELETE operations.
        config.setReadOnly(true);
        config.addDataSourceProperty("readOnlyPropagatesToServer", "true");
        config.setAutoCommit(true);
        config.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        config.setValidationTimeout(VALIDATION_TIMEOUT_MS);
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(1);
        config.setIdleTimeout(60_000);
        config.setMaxLifetime(1_800_000);
        config.setPoolName("intellij-java-sql-pool");

        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Executes a query without external values. PreparedStatement is used even
     * when there are no parameters so all query execution follows one safe path.
     */
    public List<Map<String, Object>> executeQuery(String query) {
        return executePreparedQuery(query);
    }

    /**
     * Executes parameterized SQL. Bind all untrusted values with ? placeholders;
     * SQL identifiers (table/column names) must come from a strict allow-list.
     */
    public List<Map<String, Object>> executePreparedQuery(String query, Object... parameters) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("SQL query must not be blank");
        }

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
            // Avoid logging SQL text, credentials, or parameter values.
            String sqlState = e.getSQLState() == null ? "unknown" : e.getSQLState();
            throw new IllegalStateException(
                    "Database query failed (SQLState: " + sqlState + ")", e);
        }
    }

    static String secureJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql://")) {
            throw new IllegalArgumentException("Only MySQL JDBC URLs are supported");
        }

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("^jdbc:mysql://([^/:?#]+)(?::\\d+)?(?:/|\\?|$)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(jdbcUrl);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid MySQL JDBC URL");
        }

        String host = matcher.group(1);
        boolean localHost = host.equalsIgnoreCase("localhost")
                || host.equals("127.0.0.1")
                || host.equals("::1");

        if (localHost) {
            return jdbcUrl;
        }

        java.util.regex.Matcher sslMode = java.util.regex.Pattern
                .compile("(?:[?&])sslMode=([^&]*)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(jdbcUrl);

        if (sslMode.find()) {
            if (!"VERIFY_IDENTITY".equalsIgnoreCase(sslMode.group(1))) {
                throw new IllegalArgumentException(
                        "Remote MySQL connections must use sslMode=VERIFY_IDENTITY");
            }
            return jdbcUrl;
        }

        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslMode=VERIFY_IDENTITY";
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
