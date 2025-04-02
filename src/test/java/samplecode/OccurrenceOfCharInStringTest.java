package samplecode;

public class OccurrenceOfCharInStringTest {
    public static void main(String[] args) {
        String str = "Kaustubh";
        char ch = 'a';
        int count = 0;
        for (char c : str.toCharArray()) {
            if (ch == c) {
                count++;
            }
        }
        System.out.println("Count is: " + count);
    }
}