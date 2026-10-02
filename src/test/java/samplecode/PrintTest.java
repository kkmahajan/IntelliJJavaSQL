package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PrintTest {

    @Test
    public void shouldKeepOnlyDistinctValuesInEncounterOrder() {
        int[] values = {1, 2, 3, 1, 2, 4, 5, 4};
        List<Integer> distinct = Arrays.stream(values).distinct().boxed().toList();
        Assert.assertEquals(distinct, List.of(1, 2, 3, 4, 5));
    }
}
