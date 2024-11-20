package javafeatures;

public class VowelCount {
    public static void main(String[] args) {
        String input = "Capgemini";
        System.out.println("Number of vowels: " + countVowels(input));
    }

    public static long countVowels(String str) {
        return str.chars().filter(c -> "AEIOUaeiou".indexOf(c) != -1).count();
    }
}