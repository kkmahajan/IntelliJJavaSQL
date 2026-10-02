package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class RemoveDuplicateWordsTest {

    @Test
    public void shouldCountDuplicateWordsCaseInsensitively() {
        Map<String, Integer> duplicates = findDuplicateWords("Welcome Kaustubh welcome1 March32025 Welcome welcome");
        Assert.assertEquals(duplicates, Map.of("welcome", 3));
    }

    private Map<String, Integer> findDuplicateWords(String input) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String word : input.toLowerCase().trim().split("\\s+")) {
            counts.merge(word, 1, Integer::sum);
        }
        counts.entrySet().removeIf(entry -> entry.getValue() < 2);
        return counts;
    }
}
