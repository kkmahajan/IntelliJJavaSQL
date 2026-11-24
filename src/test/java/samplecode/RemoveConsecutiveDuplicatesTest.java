package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class RemoveConsecutiveDuplicatesTest {

    @Test
    public void shouldRemoveOnlyConsecutiveDuplicates() {
        int[] values = {1, 2, 2, 3, 4, 5, 5, 3, 4, 3};
        Assert.assertEquals(removeConsecutiveDuplicates(values), List.of(1, 2, 3, 4, 5, 3, 4, 3));
    }

    private List<Integer> removeConsecutiveDuplicates(int[] values) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (i == 0 || values[i] != values[i - 1]) {
                result.add(values[i]);
            }
        }
        return result;
    }
}
