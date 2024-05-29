package org.example;

import java.sql.*;
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
            while (resultSet.next()) {
                System.out.println(resultSet.getString("empname"));
                System.out.println(resultSet.getString("empId"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL Exception occurred", e);
        }
    }
}