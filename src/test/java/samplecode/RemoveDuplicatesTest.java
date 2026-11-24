package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveDuplicatesTest {

    @Test
    public void shouldRemoveDuplicateNumbers() {
        int[] values = {1, 2, 2, 3, 4, 5};
        Set<Integer> distinct = Arrays.stream(values).boxed().collect(Collectors.toSet());
        Assert.assertEquals(distinct, Set.of(1, 2, 3, 4, 5));
    }
}
