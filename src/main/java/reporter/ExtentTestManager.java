package reporter;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static synchronized ExtentTest getExtentTest() {
        return extentTest.get();
    }

    public static synchronized void setExtentTest(ExtentTest test) {
        extentTest.set(test);
    }
}
