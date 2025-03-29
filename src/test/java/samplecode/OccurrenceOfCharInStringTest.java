package samplecode;

import org.testng.annotations.Test;

public class OccurrenceOfCharInStringTest {
    public static void main(String[] args) {
        String str = "automation";
        char ch = 'a';
        int count = 0;
        for (char c : str.toCharArray()) {
            if (ch == c) {
                count++;
            }
        }
        System.out.println("String Args[0] is: " + args[0]);
        System.out.println("Count is: " + count);
    }

    public static String compressString(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        StringBuilder compressed = new StringBuilder();
        int count = 1;

        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                count++;
            } else {
                compressed.append(str.charAt(i - 1)).append(count);
                count = 1;
            }
        }

        compressed.append(str.charAt(str.length() - 1)).append(count);
        return compressed.toString();
    }

    /**
     *
     */
    @Test
    public void testCharacterCount() {
        String str = "aaaabbbbwwwwddqasa";
        System.out.println(compressString(str));
    }
}
