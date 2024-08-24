package org.example;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.*;

public class DatabaseUtils {

    static PropertiesFileSetup propertiesFileSetup = new PropertiesFileSetup();
    static Properties prop = propertiesFileSetup.setProperties();
    final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    String dbUser = System.getenv(SQL_DB_USERNAME);
    String dbKey = System.getenv(SQL_DB_KEY);
    String connectionUrl = null;
    Connection con;
    Statement statement;
    ResultSet resultSet;

    public List<Map<String, Object>> executeQuery(String query) {

        DriverManager.setLoginTimeout(10);
        List<Map<String, Object>> resultList = null;
        try {
            connectionUrl = prop.getProperty(sqlDbUrl);
            con = DriverManager.getConnection(connectionUrl, dbUser, dbKey);
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);

            resultList = new ArrayList<>();

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
            con.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Exception occurred", e);
        }
        return resultList;
    }
}
