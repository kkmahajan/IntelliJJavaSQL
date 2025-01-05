package samplecode;

import java.util.HashMap;
import java.util.Map;

public class map {
	
	public static void main(String[] args) {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("name",1);
		map.put("city", 2);
		
//		using entrySet()
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println("values: "+entry.getKey()+ " " + entry.getValue());
		}
		
		for(Integer value: map.values()) {
			System.out.println("values: "+value);
		}
		
//		using keySet()
		
		Map<Integer, String> map1 = new HashMap<>();
		map1.put(1, "One");
		map1.put(2, "Two"); 
		map1.put(3, "Three"); 
		for (Integer key : map1.keySet()){
			String value = map1.get(key); 
			System.out.println("Key: " +key); 
			}
	
		
//		Using Values()
		
		Map<Integer, String> map3 = new HashMap<>(); 
		map3.put(1, "One");
		map3.put(2, "Two"); 
		map3.put(3, "Three"); 
		
		for (String value : map3.values()) {
			System.out.println("Value: " + value);
			}
		
	}

}
