package samplecode;

import java.util.Scanner;

public class Palindrome {

    public static void main(String[] args) {
        String str = "madam";
        String reversed = new StringBuilder(str).reverse().toString();
        System.out.println(str.equals(reversed));

        Scanner s = new Scanner(System.in);
        System.out.print(palindromeCheck(s.nextLine()));
        s.close(); // Close the scanner to prevent resource leaks
    }

    public static String palindromeCheck(String str) {
        str = str.replaceAll(" ", ""); // Remove spaces
        int varOcg = str.length(); // __define-ocg__

        for (int i = 0; i < varOcg / 2; i++) {
            if (str.charAt(i) != str.charAt(varOcg - 1 - i)) {
                return "false";
            }
        }
        return "true";
    }
}
