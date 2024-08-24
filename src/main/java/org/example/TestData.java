package org.example;

public class TestData {

    //Query Constants
    public static final String EMP_SEL = "SELECT * FROM EMPLOYEE";
    public static final String EMP_SEL_NAME = "SELECT * FROM EMPLOYEE WHERE EMP_NAME = '&empName' AND EMP_ID = '&empId' AND EMP_DEPT = '&empDept' AND EMP_AGE = '&empAge'";

    //Variable Constants
    public static final String SQL_DB_USERNAME = "SQL_DB_USERNAME";
    public static final String SQL_DB_KEY = "SQL_DB_Key";
    public static final String sqlDbUrl = "sqlDbUrl";

    //Paths
    public static final String PROP_FILE_PATH = "src/test/resources/config.properties";
    public static final String COURSE_JSON_FILE_PATH = "src/test/resources/CoursePrice.json";
}
