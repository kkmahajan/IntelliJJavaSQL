package pubmatic;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class FindIndicesWhichMatchTheSumTest {

    @Test
    public void testFindSubarraysWithSum() {
        int[] input = {3, 4, -7, 1, 3, 3, 1, -4};
        int targetSum = 7;

        List<String> result = findSubarraysWithSum(input, targetSum);

        // Print the results
        for (String res : result) {
            System.out.println(res);
        }
    }

    private List<String> findSubarraysWithSum(int[] arr, int targetSum) {
        List<String> result = new ArrayList<>();

        // Iterate through the array
        for (int start = 0; start < arr.length; start++) {
            int sum = 0;

            // Calculate the sum of subarray starting from 'start' index
            for (int end = start; end < arr.length; end++) {
                sum += arr[end];
                // If sum matches the target, add indices to the result
                if (sum == targetSum) {
                    result.add("Sum found between indexes " + start + " and " + end);
                }
            }
        }

        return result;
    }
}
