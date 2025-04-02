package samplecode;

public class findVowelsConsonants {
    public static void main(String[] args) {
        String str = "abcdefghijklmnopqrstuvwxyz";
        int vowels = 0;
        int consonants = 0;
        for (char c : str.toCharArray()) {
            if ("aeiouAEIOU".indexOf(c) != -1) {
                vowels++;
                System.out.println("Vowel : " + c);
            } else if (Character.isLetter(c)) {
                consonants++;
                System.out.println("Consonant : " + c);
            }
        }
        System.out.println("Vowels: " + vowels + " & Consonants :" + consonants);
    }
}