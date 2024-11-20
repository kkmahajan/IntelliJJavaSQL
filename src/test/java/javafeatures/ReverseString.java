package javafeatures;

import org.testng.annotations.Test;

public class ReverseString {
    public static void main(String[] args) {
        String input = "Hello, World!";  // Original string
        String reversed = reverseString(input);  // Call the reverse method
        System.out.println("Reversed String: " + reversed);  // Output the reversed string
    }

    // Method to reverse the string
    public static String reverseString(String str) {
        String reversed = "";  // Initialize an empty string to store the reversed string

        // Loop through the original string in reverse order
        for (int i = str.length() - 1; i >= 0; i--) {
            reversed += str.charAt(i);  // Append each character to the reversed string
        }

        return reversed;  // Return the reversed string
    }

    @Test
    public void testString(){
        int a = 6;
        System.out.println(" ~a = " + ~a);
    }
}
