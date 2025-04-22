package samplecode;

import java.util.Arrays;

public class checkForAnagram {
    public static void main(String[] args) {
        String s1 = "listen";
        String s2 = "silent";
        String s3 = "lisen1";

        char[] a = s1.toCharArray();
        char[] b = s2.toCharArray();
        char[] c = s3.toCharArray();

        Arrays.sort(a);
        Arrays.sort(b);
        Arrays.sort(c);

        System.out.println(Arrays.equals(a, b));
        System.out.println(Arrays.equals(a, c));
    }
}