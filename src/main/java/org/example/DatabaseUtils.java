package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {

    public static List<Map<String, Object>> executeQuery(String jdbcUrl, String username, String password, String query) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Get the metadata of the ResultSet to retrieve column names
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(resultSet.getMetaData().getColumnName(i));
            }

            // Iterate over the ResultSet and populate the list
            while (resultSet.next()) {
                Map<String, Object> rowMap = new HashMap<>();
                for (String columnName : columnNames) {
                    rowMap.put(columnName, resultSet.getObject(columnName));
                }
                resultList.add(rowMap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally, you can rethrow the exception or handle it differently
            // throw new RuntimeException("Failed to execute query", e);
        }

        return resultList;
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/dbo";
        String username = "root";
        String password = "password";
        String query = "SELECT * FROM your_table"; // Replace with your actual query

        List<Map<String, Object>> result = executeQuery(jdbcUrl, username, password, query);

        // Print the result to verify
        for (Map<String, Object> row : result) {
            System.out.println(row);
        }
    }
}
