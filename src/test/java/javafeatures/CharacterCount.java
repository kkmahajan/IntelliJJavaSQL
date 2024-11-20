package javafeatures;

import java.util.Scanner;

public class CharacterCount {

    /**
     * This method provides a simple example of counting the occurrences of a character in a string.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Input string and character
        System.out.print("Enter the string: ");
        String input = scanner.nextLine();
        System.out.print("Enter the character to count: ");
        char characterToCount = scanner.next().charAt(0);

        // Count occurrences
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == characterToCount) {
                count++;
            }
        }

        System.out.println("The character '" + characterToCount + "' appears " + count + " times in the string.");
    }
}
