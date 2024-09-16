package org.example;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.*;

public class DatabaseUtils {

    static PropertiesFileSetup propertiesFileSetup = new PropertiesFileSetup();
    static Properties prop = propertiesFileSetup.setProperties();
    final Logger LOGGER = Logger.getLogger(DatabaseUtils.class.getName());
    String dbUser = System.getenv(SQL_DB_USERNAME);
    String dbKey = System.getenv(SQL_DB_KEY);
    String connectionUrl = null;
    Connection con;
    Statement statement;
    ResultSet resultSet;

    /**
     * This method is used to execute query and provide the result in List<Map<String, Object>>
     *
     * @param query as String
     * @return Query Result List<Map<String, Object>>
     */
    public List<Map<String, Object>> executeQuery(String query) {

        DriverManager.setLoginTimeout(10);
        System.out.println("Used Query : " + query);
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

    /**
     * This method takes input a query and replaces the placeholders in the query with the actual values passed
     * as parameters in the Map<String, String>
     *
     * @param query    as String
     * @param paramMap as Map<String, String>
     * @return Query Result as List<Map<String, Object>>
     */
    public List<Map<String, Object>> getDBDataWithParams(String query, Map<String, String> paramMap) {

        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            query = query.replace(("&" + entry.getKey()), entry.getValue());
        }
        return executeQuery(query);
    }


}
