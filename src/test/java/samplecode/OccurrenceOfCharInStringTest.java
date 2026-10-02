package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OccurrenceOfCharInStringTest {

    @Test
    public void shouldCountCharacterOccurrences() {
        Assert.assertEquals(countOccurrences("Kaustubh", 'a'), 1);
        Assert.assertEquals(countOccurrences("banana", 'a'), 3);
        Assert.assertEquals(countOccurrences("", 'a'), 0);
    }

    private long countOccurrences(String input, char target) {
        return input.chars().filter(character -> character == target).count();
    }
}
