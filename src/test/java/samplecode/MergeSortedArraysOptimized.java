package samplecode;

import java.util.Arrays;
//If the input arrays are already sorted, use this two-pointer approach (faster and more efficient).

public class MergeSortedArraysOptimized {
    public static void main(String[] args) {
        int[] arr1 = {1, 3, 5, 7};  // Already sorted
        int[] arr2 = {2, 4, 6, 8};  // Already sorted

        int[] mergedArray = mergeSortedArrays(arr1, arr2);

        System.out.println("Merged and Sorted Array: " + Arrays.toString(mergedArray));
    }

    public static int[] mergeSortedArrays(int[] arr1, int[] arr2) {
        int n1 = arr1.length, n2 = arr2.length;
        int[] mergedArray = new int[n1 + n2];

        int i = 0, j = 0, k = 0;

        // Merge both arrays
        while (i < n1 && j < n2) {
            if (arr1[i] < arr2[j]) {
                mergedArray[k++] = arr1[i++];
            } else {
                mergedArray[k++] = arr2[j++];
            }
        }

        // Copy remaining elements from arr1
        while (i < n1) {
            mergedArray[k++] = arr1[i++];
        }

        // Copy remaining elements from arr2
        while (j < n2) {
            mergedArray[k++] = arr2[j++];
        }

        return mergedArray;
    }
}
