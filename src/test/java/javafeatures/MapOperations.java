package javafeatures;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MapOperations {

    @Test
    public void testMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("key", "value");
        myMap.put("key2", "value2");
        myMap.put("key3", "value3");
        System.out.println("Hashmap : " + myMap);
        myMap.putIfAbsent("key4", "value4");
        System.out.println();
        System.out.println("Hashmap1 : " + myMap);
        myMap.computeIfPresent("key4", (k, v) -> "value4");
        System.out.println("Hashmap3 : " + myMap);

        TreeMap<String, String> myMap1 = new TreeMap<>();
        myMap1.put("key", "value");
        myMap1.put("key2", "value2");
        myMap1.put("key4", "value4");
        myMap1.put("key0", "value0");
        myMap1.put("abc0", "value0");
        myMap1.put("1", "1");
        System.out.println("Tree Map : " + myMap1);
        System.out.println(myMap1.firstEntry());

        Map<String, String> myMap2 = new LinkedHashMap<>();
        myMap2.put("key", "value");
        myMap2.put("key2", "value2");
        myMap2.put("key9", "value9");
        myMap2.put("key1", "value1");
        myMap2.put("key5", "value5");
        System.out.println("Linked Hashmap : " + myMap2);
        myMap2.merge("key5", "value5", (k, v) -> "value5");
        System.out.println("Linked Hashmap : " + myMap2);
    }
}
