package org.example;

public final class TestData {

    private TestData() {
    }

    // Query constants
    public static final String EMP_SEL = "SELECT * FROM EMPLOYEE";
    public static final String EMP_SEL_NAME =
            "SELECT * FROM EMPLOYEE WHERE EMP_NAME = ? AND EMP_ID = ? AND EMP_DEPT = ? AND EMP_AGE = ?";

    // Configuration keys
    public static final String SQL_DB_USERNAME = "SQL_DB_USERNAME";
    public static final String SQL_DB_KEY = "SQL_DB_KEY";
    public static final String SQL_DB_URL = "sqlDbUrl";

    // Paths
    public static final String PROP_FILE_PATH = "src/test/resources/config.properties";
    public static final String COURSE_JSON_FILE_PATH = "src/test/resources/CoursePrice.json";
}
