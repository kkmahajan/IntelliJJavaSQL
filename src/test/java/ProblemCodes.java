import org.testng.annotations.Test;

public class ProblemCodes {

    public static boolean isFibonacci(int n) {
        if (n < 0) {
            return false; // Negative numbers are not in the Fibonacci series
        }

        // Check if 5*n*n + 4 or 5*n*n - 4 is a perfect square
        int root1 = (int) Math.sqrt(5 * n * n + 4);
        int root2 = (int) Math.sqrt(5 * n * n - 4);
        return (root1 * root1 == 5 * n * n + 4) || (root2 * root2 == 5 * n * n - 4);
    }

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

    @Test
    public void checkFibonacciNumber() {

        int n = 2;
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

    @Test
    public void testSqrt() {
        System.out.println((int) Math.sqrt(5 * 2 * 2 + 4));
        System.out.println((int) Math.sqrt(5 * 2 * 2 - 4));
        System.out.println((int) Math.sqrt(16 * 16));
    }
}
