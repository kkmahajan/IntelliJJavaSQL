import org.testng.annotations.Test;

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
}
