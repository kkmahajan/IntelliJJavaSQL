package samplecode;

public class PalindromeCheck {

    public static void main(String[] args) {
        String input = "madam";
        System.out.println("Is palindrome: " + isPalindrome(input));
    }

    public static boolean isPalindrome(String str) {
        return str.contentEquals(new StringBuilder(str).reverse());
    }
}
