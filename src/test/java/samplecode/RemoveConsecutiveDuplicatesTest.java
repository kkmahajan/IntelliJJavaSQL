package samplecode;

import java.util.ArrayList;
import java.util.List;

public class RemoveConsecutiveDuplicatesTest {
    public static List<Integer> removeConsecutiveDuplicates(int[] arr) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            if (i == 0 || arr[i] != arr[i - 1]) {
                result.add(arr[i]);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 2, 3, 4, 5, 5, 3, 4, 3};
        List<Integer> result = removeConsecutiveDuplicates(a);
        System.out.println(result);
    }
}