package javafeatures;

import org.testng.annotations.Test;

public class SystemFeaturesTest {

    @Test
    public void testSystem(){
        String osName = System.getProperty("os.name");
        String userHome = System.getProperty("user.home");
        String javaVersion = System.getProperty("java.version");
        String javaHome = System.getProperty("java.home");

        System.out.println("Operating System: " + osName);
        System.out.println("User Home Directory: " + userHome);
        System.out.println("Java Version: " + javaVersion);
        System.out.println("Java Home: " + javaHome);

        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "config.properties";
        System.out.println("File Path: " + filePath);

        String environment = System.getProperty("env", "default"); // Provide a default value
        System.out.println("Environment: " + environment);

    }

    @Test
    public void sumOfArrayItems(){
        int[] a = {10,20,30};
        int sum = 0;
        for (int j : a) {
            sum = sum + j;
        }
        System.out.println("Sum of Array Items : "+sum);
        System.out.println("No. of Array Items : "+a.length);
        System.out.println("Average of Array Items : "+sum/a.length);
    }
}
