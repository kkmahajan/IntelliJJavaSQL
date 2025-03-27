package samplecode;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveDuplicatesTest {
    public static void main(String[] args) {
        int[] a = {1, 2, 2, 3, 4, 5};
        System.out.println("arrays: " + Arrays.toString(a));
        HashSet<Integer> b = new HashSet<>();
        for (int n : a) {
            b.add(n);
        }
        System.out.println("updated set: " + b);
    }

    @Test
    public void removeDuplicatesFromArray() {
        int[] a = {1, 2, 2, 3, 4, 5};
        System.out.println("arrays: " + Arrays.toString(a));
        // Optimized: Convert array to HashSet using Streams
        Set<Integer> b = Arrays.stream(a).boxed().collect(Collectors.toSet());
        System.out.println("updated set: " + b);
    }
}