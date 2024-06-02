package org.example;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.example.TestData.EMP_SEL;


public class DatabaseConnection {

    static PropertiesFileSetup propertiesFileSetup = new PropertiesFileSetup();
    static DatabaseUtils dbUtils = new DatabaseUtils();

    public static void main(String[] args) {

        Properties prop = propertiesFileSetup.setProperties();
        List<Map<String, Object>> result = dbUtils.executeQuery(prop, EMP_SEL);

        System.out.println("Employee Id : " + result.getFirst().get("EMP_ID"));
        System.out.println("Employee Name : " + result.getFirst().get("EMP_NAME"));
        System.out.println("Employee Age : " + result.getFirst().get("EMP_AGE"));
        System.out.println("Employee Department : " + result.getFirst().get("EMP_DEPT"));

        System.out.println("Employee Id : " + result.get(1).get("EMP_ID"));
        System.out.println("Employee Name : " + result.get(1).get("EMP_NAME"));
        System.out.println("Employee Age : " + result.get(1).get("EMP_AGE"));
        System.out.println("Employee Department : " + result.get(1).get("EMP_DEPT"));

        System.out.println("First HM of First List : " + result.getFirst());
    }
}