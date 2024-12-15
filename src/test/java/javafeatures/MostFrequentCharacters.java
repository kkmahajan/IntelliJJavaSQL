package javafeatures;

import java.util.HashMap;
import java.util.Map;

public class MostFrequentCharacters {
    public static void findMostFrequentCharacters(String str) {
        // Create a HashMap to store the frequency of each character
        Map<Character, Integer> charCountMap = new HashMap<>();

        // Iterate through the string to count characters
        for (char c : str.toCharArray()) {
            // Increment count if character exists; otherwise, set to 1
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }
        System.out.println("Map : "+charCountMap);

        // Find the maximum frequency
        int maxFrequency = 0;
        for (int count : charCountMap.values()) {
            if (count > maxFrequency) {
                maxFrequency = count;
            }
        }

        // Find the characters with the maximum frequency
        System.out.println("Character(s) with the maximum frequency:");
        for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                System.out.println(entry.getKey() + " (Frequency: " + entry.getValue() + ")");
            }
        }
    }

    public static void main(String[] args) {
        String input = "Kaustubh";
        findMostFrequentCharacters(input);
    }
}
