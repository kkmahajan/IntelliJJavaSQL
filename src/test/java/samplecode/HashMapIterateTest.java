package samplecode;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HashMapIterateTest {

    @Test
    public void testHashMapIterationWithoutIterators() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        for (String key : map.keySet()) {
            System.out.println("Key: " + key + ", Value: " + map.get(key));
        }

        for (Integer value : map.values()) {
            System.out.println("Value: " + value);
        }


        map.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
    }

    @Test
    public void testHashMapIterationWithIterators() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        // Get the entrySet iterator
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        Iterator<String> keyIterator = map.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            System.out.println("Key: " + key + ", Value: " + map.get(key));
        }

        Iterator<Integer> valueIterator = map.values().iterator();
        while (valueIterator.hasNext()) {
            Integer value = valueIterator.next();
            System.out.println("Value: " + value);
        }

    }
}