package javafeatures;

import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class RemoveDuplicatesTest {

    public static void main(String[] args) {
        String input = "Capgemini";
        System.out.println("String without duplicates: " + removeDuplicates(input));
    }

    public static String removeDuplicates(String str) {
        Set<Character> seen = new LinkedHashSet<>();
        for (char c : str.toCharArray()) seen.add(c);
        StringBuilder sb = new StringBuilder();
        seen.forEach(sb::append);
        return sb.toString();
    }

    @Test
    public static void reverseStringByWord() {
        String inputString = "i love java programming";
        String[] words = inputString.split(" ");
        String reversedString = "";

        for (int i = words.length - 1; i >= 0; i--) {
            reversedString += words[i] + " ";
        }
        System.out.println("Reversed string by word: " + reversedString.trim());
    }
}
