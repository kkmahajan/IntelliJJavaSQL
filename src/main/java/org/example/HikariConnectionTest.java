package org.example;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.example.TestData.EMP_SEL;

public class HikariConnectionTest {

    @Test
    public void sqlTestingWithHikari() {
        try (DatabaseUtils dbUtils = new DatabaseUtils()) {
            List<Map<String, Object>> rows = dbUtils.executeQuery(EMP_SEL);
            for (Map<String, Object> row : rows) {
                System.out.println(row);
            }
        }
    }
}
