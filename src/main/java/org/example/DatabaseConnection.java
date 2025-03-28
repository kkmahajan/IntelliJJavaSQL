package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.TestData.EMP_SEL;
import static org.example.TestData.EMP_SEL_NAME;


public class DatabaseConnection {
    static DatabaseUtils dbUtils = new DatabaseUtils();

    public static void main(String[] args) {

        List<Map<String, Object>> result = dbUtils.executeQuery(EMP_SEL);
        System.out.println("***: JACKSON :***\n" + Utils.listOfMapToJsonStringUsingJackson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingJackson(result));
        System.out.println("***: GSON :***\n" + Utils.listOfMapToJsonStringUsingGson(result));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result));

    }

    public static void executeDbDataMethod() {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("empName", "John");
        paramMap.put("empId", "1");
        paramMap.put("empDept", "UI");
        paramMap.put("empAge", "24");

        System.out.println("\nOriginal Query with placeholders : \n" + EMP_SEL_NAME);
        List<Map<String, Object>> result2 = dbUtils.getDBDataWithParams(EMP_SEL_NAME, paramMap);
        System.out.println("\n***: GSON :***\n" + Utils.listOfMapToJsonStringUsingGson(result2));
        System.out.println(Utils.listOfMapToPrettyJsonStringUsingGson(result2));
    }

    public void executeDbDataMethodTest() {
        executeDbDataMethod();
    }
}