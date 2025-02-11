package javafeatures;

import org.testng.annotations.Test;

public class VowelCount {
    public static void main(String[] args) {
        String input = "Kaustubh";
        System.out.println("Number of vowels: " + countVowels(input));
    }

    public static long countVowels(String str) {
        return str.toLowerCase().chars().filter(c -> "aeiou".indexOf(c) != -1).count();
    }

    @Test
    public void findVowels() {
        // Convert string to lowercase for uniformity
        String str = "Kaustubh";
        str = str.toLowerCase();

        // Define vowels
        String vowels = "aeiou";

        // Loop through the string
        System.out.println("Vowels in the string \"" + str + "\":");
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (vowels.indexOf(ch) != -1) { // Check if character is a vowel
                count++;
                System.out.print(ch + " ");
            }
        }
        System.out.println("\nTotal Vowels in String :" + count);
    }
}