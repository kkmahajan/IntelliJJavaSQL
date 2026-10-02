package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ArraysWithStreamTest {

    @Test
    public void shouldSortStrings() {
        List<String> values = List.of("kaustubh", "vaibhav", "shubham", "krishna", "ashish");
        Assert.assertEquals(values.stream().sorted().toList(), List.of("ashish", "kaustubh", "krishna", "shubham", "vaibhav"));
    }

    @Test
    public void shouldRemoveDuplicateArrayValues() {
        int[] values = {1, 2, 3, 1, 2, 4};
        Assert.assertEquals(Arrays.stream(values).distinct().boxed().toList(), List.of(1, 2, 3, 4));
    }

    @Test
    public void shouldTransformAndReduceValues() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        Assert.assertEquals(numbers.stream().map(number -> number * number).toList(), List.of(1, 4, 9, 16, 25));
        Assert.assertEquals(numbers.stream().mapToInt(Integer::intValue).sum(), 15);
    }

    @Test
    public void shouldJoinStrings() {
        Assert.assertEquals(String.join(", ", List.of("John", "Jane", "Jack")), "John, Jane, Jack");
    }
}
