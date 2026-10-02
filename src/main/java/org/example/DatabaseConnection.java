package org.example;

import java.util.List;
import java.util.Map;

import static org.example.TestData.EMP_SEL;
import static org.example.TestData.EMP_SEL_NAME;

public class DatabaseConnection {

    public static void main(String[] args) {
        try (DatabaseUtils dbUtils = new DatabaseUtils()) {
            List<Map<String, Object>> result = dbUtils.executeQuery(EMP_SEL);

            System.out.println("***: JACKSON :***\n" + Utils.listOfMapToJsonStringUsingJackson(result));
            System.out.println(Utils.listOfMapToPrettyJsonStringUsingJackson(result));
            System.out.println("***: GSON :***\n" + Utils.listOfMapToJsonStringUsingGson(result));
            System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result));
        }
    }

    public static void executeDbDataMethod() {
        try (DatabaseUtils dbUtils = new DatabaseUtils()) {
            List<Map<String, Object>> result = dbUtils.executePreparedQuery(
                    EMP_SEL_NAME,
                    "John",
                    1,
                    "UI",
                    24
            );

            System.out.println("\n***: GSON :***\n" + Utils.listOfMapToJsonStringUsingGson(result));
            System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result));
        }
    }

    public void executeDbDataMethodTest() {
        executeDbDataMethod();
    }
}
