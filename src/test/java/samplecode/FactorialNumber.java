package samplecode;

public class FactorialNumber {
    public static void main(String[] args) {
        int n = 5;
        int sum = 1;
        for (int i = 1; i <= n; i++) {
            sum *= i;
        }
        System.out.println("Factorial of " + n + " is: " + sum);

        System.out.println("Factorial of " + n + " using recursive method : " + factorial(n));
    }

    // Recursive Method
    public static int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }
}