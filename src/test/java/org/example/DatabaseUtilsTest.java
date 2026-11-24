package org.example;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.example.TestData.EMP_SEL;
import static org.example.TestData.EMP_SEL_NAME;
import static org.example.TestData.SQL_DB_KEY;
import static org.example.TestData.SQL_DB_USERNAME;

public class DatabaseUtilsTest {

    @Test
    public void shouldExecuteSimpleQueryUsingConnectionPool() {
        requireDatabaseCredentials();

        try (DatabaseUtils dbUtils = new DatabaseUtils()) {
            List<Map<String, Object>> rows = dbUtils.executeQuery(EMP_SEL);
            Assert.assertNotNull(rows, "Query result should never be null");
        }
    }

    @Test
    public void shouldExecuteParameterizedQuerySafely() {
        requireDatabaseCredentials();

        try (DatabaseUtils dbUtils = new DatabaseUtils()) {
            List<Map<String, Object>> rows = dbUtils.executePreparedQuery(
                    EMP_SEL_NAME,
                    "John",
                    1,
                    "UI",
                    24
            );

            Assert.assertNotNull(rows, "Prepared query result should never be null");
        }
    }

    private void requireDatabaseCredentials() {
        if (isBlank(System.getenv(SQL_DB_USERNAME)) || isBlank(System.getenv(SQL_DB_KEY))) {
            throw new SkipException(
                    "Database integration test skipped: set SQL_DB_USERNAME and SQL_DB_KEY to run it.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
