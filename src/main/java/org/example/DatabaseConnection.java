package org.example;

import java.util.List;
import java.util.Map;

import static org.example.TestData.EMP_SEL;


public class DatabaseConnection {

    public static void main(String[] args) {
        DatabaseUtils dbUtils = new DatabaseUtils();
        List<Map<String, Object>> result = dbUtils.executeQuery(EMP_SEL);
        System.out.println("***: JACKSON :***\n" + Utils.listOfMapToJsonStringUsingJackson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingJackson(result));
        System.out.println("***: GSON :***\n" + Utils.listOfMapToJsonStringUsingGson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result));
    }
}