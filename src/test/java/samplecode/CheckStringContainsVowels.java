package samplecode;

public class CheckStringContainsVowels {

    public static void main(String[] args) {
        String str = "bAbIbEbb";
        str = str.toLowerCase();
        boolean vowels = false;
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if ("aeiou".contains(Character.toString(a))) {
                System.out.println(a + " char is a vowel");
                vowels = true;
                break; //Finds the first vowel and stops execution. If not required, remove to check all vowels
            }
        }
        System.out.println("String contains Vowels is: " + vowels);
    }


}
