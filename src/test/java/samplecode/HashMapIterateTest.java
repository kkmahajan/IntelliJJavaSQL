package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class HashMapIterateTest {

    @Test
    public void shouldExposeSameEntriesThroughMapViews() {
        Map<String, Integer> values = new HashMap<>(Map.of("A", 1, "B", 2));

        Assert.assertEquals(values.entrySet().size(), 2);
        Assert.assertEquals(values.keySet(), java.util.Set.of("A", "B"));
        Assert.assertTrue(values.values().containsAll(java.util.List.of(1, 2)));
    }

    @Test
    public void shouldIterateOverEveryEntry() {
        Map<String, Integer> values = new HashMap<>(Map.of("A", 1, "B", 2));
        int sum = 0;

        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            sum += entry.getValue();
        }

        Assert.assertEquals(sum, 3);
    }
}
