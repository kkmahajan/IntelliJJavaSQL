package org.example;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
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
            e.printStackTrace();
            System.out.println("Connection Failed.");
        }
    }
}