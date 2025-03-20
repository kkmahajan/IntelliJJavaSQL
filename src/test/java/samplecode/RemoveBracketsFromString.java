package samplecode;

import java.util.Scanner;

public class RemoveBracketsFromString {

    public static int RemoveBrackets(String str) {
        int varOcg = 0; // __define-ocg__
        int openCount = 0;

        for (char ch : str.toCharArray()) {
            if (ch == '(') {
                openCount++;
            } else if (ch == ')') {
                if (openCount > 0) {
                    openCount--; // Match found, remove one opening bracket
                } else {
                    varOcg++; // Extra closing bracket
                }
            }
        }

        varOcg += openCount; // Add remaining unmatched opening brackets
        return varOcg;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print(RemoveBrackets(s.nextLine()));
        // Sample Input : (())()(((, (()(,
        s.close(); // Close the scanner to prevent resource leaks
    }
}
