package samplecode;

public class MostFrequentChar {

    public static void main(String[] args) {
        String input = "Kaustubh";
        System.out.println("Most frequent character: " + getMaxOccurrenceCharacter(input));
    }

    public static char getMaxOccurrenceCharacter(String str) {
        int[] count = new int[256];
        for (char c : str.toCharArray()) {
            count[c]++;
        }
        char maxChar = ' ';
        int maxCount = 0;
        for (int i = 0; i < count.length; i++) {
            if (count[i] > maxCount) {
                maxCount = count[i];
                maxChar = (char) i;
            }
        }
        return maxChar;
    }

    //Create a method to count the print the most occurring character from a string. If there is a tie, print the characters that are tie.


}
