package samplecode;

import org.testng.annotations.Test;

public class FibonacciSeriesTest {

    public static void main(String[] args) {
        printFibonacciSeries(7);
    }

    /**
     * This method is printing 10 numbers of fibonacci series
     */
    @Test
    public static void fibonacci() {
        int n = 10;
        int num1 = 0;
        int num2 = 1;
        System.out.print("Fibonacci Series: " + num1 + ", " + num2 + ",");
        for (int i = 2; i < n; i++) {
            int num3 = num1 + num2;
            System.out.print(num3);
            System.out.print(", ");
            num1 = num2;
            num2 = num3;
        }
    }

    /**
     * This method is printing n numbers of fibonacci series
     * @param n as int
     */
    private static void printFibonacciSeries(int n) {
        if (n <= 0) {
            System.out.println("Please enter a positive integer.");
            return;
        }
        int a = 0, b = 1;
        System.out.print("\nFibonacci Series for " + n + " digits : ");
        for (int i = 0; i < n; i++) {
            System.out.print(a + (i < n - 1 ? ", " : "\n"));
            int c = a + b;
            a = b;
            b = c;
        }
    }

    @Test
    public void printFibonacciSeriesUsingForLoop() {
        printFibonacciSeries(10);
        printFibonacciSeries(6);
    }
}
