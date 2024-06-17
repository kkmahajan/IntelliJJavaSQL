import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ProblemCodesTest {

    /**
     * This method checks if a number is fibonacci or not using Java
     * @param n as int
     * @return boolean
     */
    public static boolean isFibonacci(int n) {
        if (n < 0) {
            return false; // Negative numbers are not in the Fibonacci series
        }

        // Check if 5*n*n + 4 or 5*n*n - 4 is a perfect square
        int root1 = (int) Math.sqrt(5 * n * n + 4);
        int root2 = (int) Math.sqrt(5 * n * n - 4);
        return (root1 * root1 == 5 * n * n + 4) || (root2 * root2 == 5 * n * n - 4);
    }

    /**
     * This method checks if a number is fibonacci or not using Java
     * @param n as int
     * @return boolean
     */
    public static boolean isFibonacciJava(int n) {
        if (n < 0) {
            return false; // Negative numbers are not in the Fibonacci series
        }

        int a = 0;
        int b = 1;
        while (a <= n) {
            if (a == n) {
                return true;
            }
            int c = a + b;
            a = b;
            b = c;
        }
        return false;
    }

    /**
     * This method checks if a number is fibonacci or not and uses other methods to prove it
     */
    @Test
    public void checkFibonacciNumber() {

        int n = 20000;
        if (isFibonacci(n)) {
            System.out.println("Given number is fibonacci");
        } else {
            System.out.println("Given number is not in fibonacci");
        }

        if (isFibonacciJava(n)) {
            System.out.println("Java : Given number is fibonacci");
        } else {
            System.out.println("Java : Given number is not in fibonacci");
        }
    }


    /**
     * This test method demonstrates the usage of the Math.sqrt() method.
     * It calculates the square root of different numbers and prints the results.
     */
    @Test
    public void testSqrt() {
        // Calculate and print the square root of 5 * 2 * 2 + 4
        int num1 = 5 * 2 * 2 + 4;
        int sqrt1 = (int) Math.sqrt(num1);
        System.out.println(sqrt1);

        // Calculate and print the square root of 5 * 2 * 2 - 4
        int num2 = 5 * 2 * 2 - 4;
        int sqrt2 = (int) Math.sqrt(num2);
        System.out.println(sqrt2);

        // Calculate and print the square root of 16 * 16
        int num3 = 16 * 16;
        int sqrt3 = (int) Math.sqrt(num3);
        System.out.println(sqrt3);
    }

    /**
     * This test method demonstrates the usage of the Math.pow() method.
     * It calculates the power of 2 and 3 and prints the results.
     */
    @Test
    public void testPower() {
        System.out.println(Math.pow(2,3));
    }

    // Write a method to check and print the repeating characters with the number of occurrences in a string using Java
    @Test
    public void testRepeatingCharacters() {
        String str = "Kaustubh";
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            for (int j = i + 1; j < chars.length; j++) {
                if (chars[i] == chars[j]) {
                    System.out.println(chars[i]);
                }
            }
        }
    }

    @Test
    public void testMap(){
        Map<String, String> myMap = new HashMap<>();
        myMap.put("key", "value");
        myMap.put("key2", "value2");
        myMap.put("key3", "value3");
        System.out.println("Hashmap : "+myMap);
        myMap.putIfAbsent("key4", "value4");
        System.out.println();
        System.out.println("Hashmap1 : "+myMap);
        myMap.computeIfPresent("key4", (k, v) -> "value4");
        System.out.println("Hashmap3 : "+myMap);

        TreeMap<String, String> myMap1 = new TreeMap<>();
        myMap1.put("key", "value");
        myMap1.put("key2", "value2");
        myMap1.put("key4", "value4");
        myMap1.put("key0", "value0");
        myMap1.put("abc0", "value0");
        myMap1.put("1", "1");
        System.out.println("Tree Map : "+myMap1);
        System.out.println(myMap1.firstEntry());

        Map<String, String> myMap2 = new LinkedHashMap<>();
        myMap2.put("key", "value");
        myMap2.put("key2", "value2");
        myMap2.put("key9", "value9");
        myMap2.put("key1", "value1");
        myMap2.put("key5", "value5");
        System.out.println("Linked Hashmap : "+myMap2);
        myMap2.merge("key5", "value5", (k, v) -> "value5");
        System.out.println("Linked Hashmap : "+myMap2);
    }
}
