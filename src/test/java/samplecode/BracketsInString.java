package samplecode;

import java.util.Stack;

public class BracketsInString {


    public static void main(String[] args) {
        String input = "(a(b)cd";

        // Process the input string // Output = (ab)cd
        String result = removeUnmatchedBrackets(input); // Output = (ab)cd
        System.out.println("Result: " + result); // Output = (ab)cd

        // Process the input string // Output = a(b)cd
        String resultSecond = removeUnmatchedBracketsSecond(input); // Output = a(b)cd
        System.out.println("Result: " + resultSecond); // Output = a(b)cd
    }

    private static String removeUnmatchedBrackets(String str) {
        StringBuilder result = new StringBuilder();
        int openCount = 0;

        // Loop through characters in the string
        for (char ch : str.toCharArray()) {
            if (Character.isAlphabetic(ch)) {
                result.append(ch); // Append alphabets
            } else if (ch == '(') {
                openCount++;
                result.append(ch); // Append valid opening brackets
            } else if (ch == ')') {
                if (openCount > 0) { // Ensure matching
                    openCount--;
                    result.append(ch); // Append valid closing brackets
                }
            }
        }

        // Remove unmatched opening brackets
        for (int i = result.length() - 1; i >= 0 && openCount > 0; i--) {
            if (result.charAt(i) == '(') {
                result.deleteCharAt(i);
                openCount--;
            }
        }

        return result.toString();
    }

    private static String removeUnmatchedBracketsSecond(String str) {
        StringBuilder result = new StringBuilder();
        Stack<Integer> stack = new Stack<>(); // Stack to track indices of unmatched '('

        // Step 1: First pass - Identify valid brackets and alphabets
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isAlphabetic(ch)) {
                result.append(ch); // Append alphabetic characters
            } else if (ch == '(') {
                stack.push(result.length()); // Store index of '(' in the result
                result.append(ch);
            } else if (ch == ')') {
                if (!stack.isEmpty()) { // Match closing bracket if stack is not empty
                    stack.pop(); // Remove matched '(' from stack
                    result.append(ch); // Append valid closing bracket
                }
            }
        }

        // Step 2: Remove unmatched '(' using the stack
        while (!stack.isEmpty()) {
            result.deleteCharAt(stack.pop()); // Remove unmatched '('
        }

        return result.toString();
    }
}

