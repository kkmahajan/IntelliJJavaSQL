package javafeatures;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapOperations {

    @Test
    public void shouldSupportCommonMapOperations() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("key", "value");
        values.putIfAbsent("key2", "value2");
        values.computeIfPresent("key2", (key, value) -> value.toUpperCase());
        values.merge("key3", "value3", (oldValue, newValue) -> newValue);

        Assert.assertEquals(values.get("key"), "value");
        Assert.assertEquals(values.get("key2"), "VALUE2");
        Assert.assertEquals(values.get("key3"), "value3");
    }

    @Test
    public void shouldSortTreeMapByKey() {
        TreeMap<String, String> values = new TreeMap<>(Map.of("key4", "value4", "key0", "value0", "abc0", "value0"));
        Assert.assertEquals(values.firstKey(), "abc0");
        Assert.assertEquals(values.lastKey(), "key4");
    }
}
