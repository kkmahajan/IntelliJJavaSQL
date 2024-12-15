package javafeatures;

import org.testng.annotations.Test;

import java.util.Arrays;

public class ReverseLastWordManualTest {

    @Test
    public void reverseLastWordWithoutUsingDefaultMethods() {
        String input = "today is thursday";

        // Find the last space in the string
        int lastSpaceIndex = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                lastSpaceIndex = i;
            }
        }

        // Separate the sentence into two parts
        StringBuilder firstPart = new StringBuilder();
        for (int i = 0; i < lastSpaceIndex; i++) {
            firstPart.append(input.charAt(i));
        }

        // Extract and reverse the last word
        StringBuilder lastWord = new StringBuilder();
        for (int i = lastSpaceIndex + 1; i < input.length(); i++) {
            lastWord.insert(0, input.charAt(i));
        }

        // Combine the first part and the reversed last word
        String result = firstPart + " " + lastWord;

        System.out.println(result); // Output: "today is yadsruht"
    }

    @Test
    public void reverseLastWordUsingDefaultMethods() {
        String input = "today is thursday";
        String[] words = input.split(" ");
        String lastWord = words[words.length - 1];

        // Reverse the last word
        String reversedLastWord = new StringBuilder(lastWord).reverse().toString();

        // Replace the last word with the reversed version
        words[words.length - 1] = reversedLastWord;

        // Join the words back into a single string
        String result = String.join(" ", words);

        System.out.println(result); // Output: "today is yadsruht"
    }

    @Test
    public void testString(){
        String input = "today is thursday";
        String[] wordsInString = input.split(" ");
        System.out.println(Arrays.toString(wordsInString));
        System.out.println(wordsInString[0]);
        System.out.println(wordsInString[wordsInString.length-1].toString().toUpperCase());

        String reversedLastWord = new StringBuilder(wordsInString[wordsInString.length-1]).reverse().toString();
        System.out.println(reversedLastWord.toUpperCase());
    }

    @Test
    public void testStringReverseEveryWordOnSamePosition(){
        String input = "today is my day";
        String[] wordsInString = input.split(" ");
        StringBuilder reversedString = new StringBuilder();
        for (String s : wordsInString) {
            String reversedWord = new StringBuilder(s).reverse().toString();
            reversedString.append(reversedWord).append(" ");
        }
        System.out.println(reversedString);
    }

    @Test
    public void testStringReverseEntireStatement(){
        String input = "today is my day";
        String reversedString = new StringBuilder(input).reverse().toString();
        System.out.println(reversedString);
    }

    @Test
    public void testStringReverseWordsInSentence(){
        String input = "today is my day";
        String[] wordsInString = input.split(" ");
        StringBuilder reversedString = new StringBuilder();
        for (String s : wordsInString) {
            String reversedWord = new StringBuilder(s).reverse().toString();
            reversedString.append(reversedWord).append(" ");
        }
        System.out.println(reversedString); //Output: "yadot si ym yad "
    }

    @Test
    public void reverseStringByWord() {
        String inputString = "i love java programming";
        String[] words = inputString.split(" ");
        StringBuilder reversedString = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            reversedString.append(words[i]).append(" ");
        }
        System.out.println(reversedString);
    }
}

