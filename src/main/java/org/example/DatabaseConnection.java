package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.example.TestData.EMP_SEL;


public class DatabaseConnection {

    static PropertiesFileSetup propertiesFileSetup = new PropertiesFileSetup();
    static DatabaseUtils dbUtils = new DatabaseUtils();

    public static void main(String[] args) throws JsonProcessingException {

        Properties prop = propertiesFileSetup.setProperties();
        List<Map<String, Object>> result = dbUtils.executeQuery(prop, EMP_SEL);
        System.out.println("***: JACKSON :***\n"+Utils.listOfMapToJsonStringUsingJackson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingJackson(result));
        System.out.println("***: GSON :***\n"+Utils.listOfMapToJsonStringUsingGson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result));
    }
}