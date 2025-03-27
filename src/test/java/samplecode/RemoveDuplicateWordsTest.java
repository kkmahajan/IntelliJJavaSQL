package samplecode;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class RemoveDuplicateWordsTest {

    public static void findDuplicateWords(String str) {
        String[] words = str.toLowerCase().split("\\s+");
        //String[] words = str.split("\\s+"); // using toLowerCase() ignores the case of the strings
        Map<String, Integer> wordCountMap = new HashMap<>();

        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }

        System.out.println("Duplicate words in the given string:");
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    @Test
    public void testFindDuplicateWords() {
        String string = "Welcome Kaustubh welcome1 March32025 Welcome welcome";
        findDuplicateWords(string);

        System.out.println("Array split and added in one string : \n\n" + String.join(" ", string.split("\\s+")));
    }
}