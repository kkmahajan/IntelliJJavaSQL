package samplecode;

public class PrimeChecker {
    public static boolean isPrime(int num) {
        if (num < 2) {
            return false; // numbers less than 2 are not prime
        }
        if (num == 2) {
            return true; // 2 is prime
        }
        if (num % 2 == 0) {
            return false; // even numbers (except 2) are not prime
        }
        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) {
                return false; // if number is divisible by any odd factor, it's not prime
            }
        }
        return true; // if no factors found, number is prime
    }

    public static void main(String[] args) {
        int num = 101; // example number to check
        boolean isPrime = isPrime(num);
        System.out.println(num + " is " + (isPrime ? "prime" : "not prime"));
    }
}