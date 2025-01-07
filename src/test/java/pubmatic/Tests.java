package pubmatic;

import org.testng.annotations.Test;

public class Tests {
    /**
     * Given a string, reverse the string in a way that special characters are not affected.
     * <p>
     * Input:   str = "A/B$C"
     * Output:  str = "C/B$A"
     */

    @Test
    public void reverseStringWithoutAffectingSpecialCharacters() {
        String str = "A/B$C";
        char[] newString = new char[str.length()];
        char[] c = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {
            int length = str.length() - 1 - i;
            if (c[i] > 'Z' || c[i] < 'a') {
                if (c[i] < '0' || c[i] > '9') {
                    newString[length] = c[i];
                }
            } else {
                newString[i] = c[i];
            }
        }
        System.out.println(String.valueOf(newString));
    }

    /**
     * Find indices for contiguous subarrays in an array for input sum
     * Input - [99, 4, 0, 0, 3, 10, 55], sum = 7
     * Output -  Sum found between indexes 1 and 4
     * <p>
     * Input_1 - [1, 3] , sum = 0
     * Output -  No sublist found
     * <p>
     * Input_2 - [ 3, 4, -7, 1, 3, 3, 1, -4 ], sum = 7
     * Output -  Sum found between indexes 0 and 1
     * Sum found between indexes 0 and 5
     * Sum found between indexes 3 and 5
     * Sum found between indexes 4 and 6
     */
    @Test
    public void findSubArraysWhereSumIsMatching() {
        int[] a = {3, 4, -7, 1, 3, 3, 1, -4};
        int sum = 7;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] + a[j] == sum) {
                    System.out.println("Sum found between indexes " + i + " and " + j);
                }
            }
        }
    }
}
