package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.*;

public class DatabaseConnection {
    public static void main(String[] args) {

        final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
        String dbUser = System.getenv(SQL_DB_USERNAME);
        String dbKey = System.getenv(SQL_DB_KEY);
        Properties prop = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(PROP_FILE_PATH);
            prop.load(fileInputStream);
        } catch (FileNotFoundException fnfe) {
            LOGGER.log(Level.SEVERE, "Properties file not found on the given location", fnfe);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DriverManager.setLoginTimeout(10);
        try {
            String connectionUrl = prop.getProperty(sqlDbUrl);
            Connection con = DriverManager.getConnection(connectionUrl, dbUser, dbKey);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(EMP_SEL);
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

            // getFirst() method can be used to get the first element from a list
            System.out.println("First Row : " + resultList.getFirst());
            System.out.println("First Emp Name : " + resultList.getFirst().get("EmpName"));
            System.out.println("First Emp Id Class : " + resultList.getFirst().get("EmpId").getClass());
            System.out.println("First Emp Id : " + resultList.getFirst().get("EmpId"));

            // Following code will print the result of the result set one by one.
            /*
            while (resultSet.next()) {
                System.out.println(resultSet.getString("EmpName"));
                System.out.println(resultSet.getString("EmpId"));
            }
            */
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Exception occurred", e);
        }
    }
}