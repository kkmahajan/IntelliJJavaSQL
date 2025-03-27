package samplecode;

import java.util.Arrays;

// If the input arrays are not sorted, use this approach (Arrays.sort()).
public class MergeSortedArrays {
    public static void main(String[] args) {
        int[] arr1 = {1, 5, 3, 7};
        int[] arr2 = {2, 6, 4, 8};

        // Merge and sort using built-in method
        int[] mergedArray = mergeAndSort(arr1, arr2);

        // Print the sorted merged array
        System.out.println("Merged and Sorted Array: " + Arrays.toString(mergedArray));
    }

    public static int[] mergeAndSort(int[] arr1, int[] arr2) {
        int n1 = arr1.length, n2 = arr2.length;
        int[] mergedArray = new int[n1 + n2];

        // Copy elements of both arrays
        System.arraycopy(arr1, 0, mergedArray, 0, n1);
        System.arraycopy(arr2, 0, mergedArray, n1, n2);

        // Sort the merged array
        Arrays.sort(mergedArray);

        return mergedArray;
    }
}

