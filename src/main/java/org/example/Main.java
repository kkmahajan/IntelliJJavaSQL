package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
        DriverManager.setLoginTimeout(10);
        try {
            String connectionUrl = "jdbc:mysql://localhost:3306/dbo";
            Connection con = DriverManager.getConnection(connectionUrl, "root", "admin");
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE");
            List<Map<String, Object>> resultList = new ArrayList<>();

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
            System.out.println("First Row : " + resultList.getFirst());
            System.out.println("First Emp Name : " + resultList.getFirst().get("EmpName"));
            System.out.println("First Emp Id Class : " + resultList.getFirst().get("EmpId").getClass());
            System.out.println("First Emp Id : " + resultList.getFirst().get("EmpId"));

            // Following code will print the result of the result set one by one.
            /*
            while (resultSet.next()) {
                System.out.println(resultSet.getString("empname"));
                System.out.println(resultSet.getString("empId"));
            }
            */
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Exception occurred", e);
        }
    }
}