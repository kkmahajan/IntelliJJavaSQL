package pubmatic;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class FindIndicesWhichMatchTheSumTest {

    @Test
    public void shouldFindAllContiguousRangesMatchingTargetSum() {
        int[] input = {3, 4, -7, 1, 3, 3, 1, -4};

        List<IndexRange> result = findSubarraysWithSum(input, 7);

        Assert.assertEquals(
                result,
                List.of(
                        new IndexRange(0, 1),
                        new IndexRange(0, 6),
                        new IndexRange(1, 5),
                        new IndexRange(3, 5),
                        new IndexRange(4, 6)
                )
        );
    }

    @Test
    public void shouldReturnEmptyListWhenNoRangeMatches() {
        Assert.assertTrue(findSubarraysWithSum(new int[]{1, 2, 3}, 100).isEmpty());
    }

    private List<IndexRange> findSubarraysWithSum(int[] values, int targetSum) {
        List<IndexRange> matches = new ArrayList<>();

        for (int start = 0; start < values.length; start++) {
            int sum = 0;
            for (int end = start; end < values.length; end++) {
                sum += values[end];
                if (sum == targetSum) {
                    matches.add(new IndexRange(start, end));
                }
            }
        }
        return matches;
    }

    private record IndexRange(int start, int end) {
    }
}
