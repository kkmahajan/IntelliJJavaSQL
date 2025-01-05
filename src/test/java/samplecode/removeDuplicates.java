package samplecode;

import java.util.Arrays;
import java.util.HashSet;

public class removeDuplicates {
	public static void main(String[] args) {
		int[] a = {1,2,2,3,4,5};
		System.out.println("arrays: "+Arrays.toString(a));
		HashSet<Integer> b = new HashSet<>();
		for(int n:a) {
			b.add(n);
		}
		System.out.println("updated set: "+b);
	}

}
