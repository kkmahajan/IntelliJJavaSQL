package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HikariConnectionTest {

    @Test
    public static void sqlTestingWithHikari() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dbo");
        config.setUsername("root");
        config.setPassword("admin");

        // Set the connection timeout
        config.setConnectionTimeout(10000); // 10 seconds

        DataSource dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Connected to the database");
            // Create a statement and set query timeout
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(5); // 5 seconds
                ResultSet resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("empname"));
                    System.out.println(resultSet.getString("empId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
