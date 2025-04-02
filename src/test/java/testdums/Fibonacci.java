package testdums;

import java.util.HashMap;

public class Fibonacci {
    private final HashMap<Integer, Integer> memo = new HashMap<>();

    public static void main(String[] args) {

        new Fibonacci().printFibonacciMemoized(10);
    }

    public int fibonacciMemoized(int n) {
        if (n <= 1) {
            return n;
        }
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        int result = fibonacciMemoized(n - 1) + fibonacciMemoized(n - 2);
        memo.put(n, result);
        return result;
    }

    public void printFibonacciMemoized(int n) {
        System.out.println("Fibonacci Series (Memoized):");
        for (int i = 0; i < n; i++) {
            System.out.print(fibonacciMemoized(i) + (i < n - 1 ? ", " : "\n"));
        }
    }
}

