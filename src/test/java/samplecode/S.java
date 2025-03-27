package samplecode;

import java.util.HashMap;

public class S {
    public static void main(String[] args) {
        String str1 = "google";
        String str2 = "gogoel";
        String str3 = "gogle";

        System.out.println(compareString(str1, str2)); // true
        System.out.println(compareString(str1, str3)); // false
    }

    public static boolean compareString(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }

        HashMap<Character, Integer> charCountMap = new HashMap<>();
        // Count frequency of each character in the first string
        for (char c : s1.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }
        System.out.println("Map after S1 : " + charCountMap);
        // Decrease frequency based on second string
        for (char c : s2.toCharArray()) {
            if (!charCountMap.containsKey(c) || charCountMap.get(c) == 0) {
                return false;
            }
            charCountMap.put(c, charCountMap.get(c) - 1);
        }
        System.out.println("Map after S2 : " + charCountMap);
        return true;
    }
}
