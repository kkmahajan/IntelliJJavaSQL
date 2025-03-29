package samplecode;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class Array {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] b = {1, 2, 3};
        System.out.println(Arrays.compare(a, b));
        arrayOps();
    }

    public static void arrayOps() {
        int[] a = new int[10];
        int[] b = {1, 2, 2, 3, 4, 5, 5, 3, 4, 3};
        int[] c = new int[]{};
        int[] d = new int[]{5, 42, 2, 21, 1};
        Arrays.sort(d);
        Integer[] newArray = {5, 42, 2, 21, 1};
        Arrays.sort(newArray, Collections.reverseOrder());
        int[] e = new int[]{1, 2, 3, 4, 5, 6,};
        int[] g = new int[10];
        Arrays.fill(g, -1); // Update the default value in the array for all elements
//      int[] f = new int[]; // Illegal initialization of array as length is not specified
        System.out.println("Array a[] : " + Arrays.toString(a));
        System.out.println("Array b[] : " + Arrays.toString(b));
        System.out.println("Array b[] sorted by Arrays.stream(b).sorted().toArray() : " + Arrays.toString(Arrays.stream(b).sorted().toArray()));
        System.out.println("Array c[] : " + Arrays.toString(c) + " -- Length of c[] : " + c.length);
        System.out.println("Array d[] sorted by Arrays.sort(d): " + Arrays.toString(d));
        System.out.println("Array e[] : " + Arrays.toString(e));
        System.out.println("Array g[] : " + Arrays.toString(g));
        System.out.println("Array newArray[] sorted in descending order by Arrays.sort(newArray, Collections.reverseOrder()): " + Arrays.toString(newArray));

        Integer[] defaultArray = {3, 1, 56, 123, 11, 2, 5};
        Arrays.sort(defaultArray);
        System.out.println("Array defaultArray[] sorted by Arrays.sort(defaultArray): " + Arrays.toString(defaultArray));
        printArray(defaultArray);
        Arrays.asList(defaultArray);
        Collections.reverse(Arrays.asList(defaultArray));
        printArray(defaultArray);

        int[] arr = {1, 2, 3, 4, 5};

        // Store the reversed array in a new variable
        int[] reversedArr = IntStream.range(0, arr.length)
                .map(i -> arr[arr.length - 1 - i])
                .toArray();

        System.out.println("reversedArr : " + Arrays.toString(reversedArr)); // Output: [5, 4, 3, 2, 1]
    }

    private static void printArray(Integer[] defaultArray) {
        for (int i = 0; i < defaultArray.length; i++) {
            System.out.println("defaultArray[" + i + "] : " + defaultArray[i]);
        }
    }
}
