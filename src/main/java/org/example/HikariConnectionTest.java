package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.*;

public class HikariConnectionTest {

    static PropertiesFileSetup propertiesFileSetup = new PropertiesFileSetup();
    static final Logger LOGGER = Logger.getLogger(HikariConnectionTest.class.getName());

    @Test
    public static void sqlTestingWithHikari() {

        Properties prop = propertiesFileSetup.setProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(prop.getProperty(sqlDbUrl));
        config.setUsername(System.getenv(SQL_DB_USERNAME));
        config.setPassword(System.getenv(SQL_DB_KEY));

        // Set the connection timeout
        config.setConnectionTimeout(10000); // 10 seconds

        DataSource dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            LOGGER.log(Level.INFO, "Connected to the database");
            // Create a statement and set query timeout
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(5); // 5 seconds
                ResultSet resultSet = statement.executeQuery(EMP_SEL);
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("EMP_ID"));
                    System.out.println(resultSet.getString("EMP_NAME"));
                    System.out.println(resultSet.getString("EMP_DEPT"));
                    System.out.println(resultSet.getString("EMP_AGE"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
