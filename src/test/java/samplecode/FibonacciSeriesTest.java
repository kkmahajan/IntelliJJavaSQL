package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class FibonacciSeriesTest {

    @Test
    public void shouldGenerateFirstTenFibonacciNumbers() {
        Assert.assertEquals(
                fibonacci(10),
                List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
        );
    }

    @Test
    public void shouldReturnEmptyListForNonPositiveLength() {
        Assert.assertTrue(fibonacci(0).isEmpty());
        Assert.assertTrue(fibonacci(-1).isEmpty());
    }

    private List<Integer> fibonacci(int length) {
        if (length <= 0) {
            return List.of();
        }

        List<Integer> numbers = new ArrayList<>(length);
        int previous = 0;
        int current = 1;

        for (int i = 0; i < length; i++) {
            numbers.add(previous);
            int next = previous + current;
            previous = current;
            current = next;
        }
        return numbers;
    }
}
