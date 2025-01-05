package samplecode;

import java.util.Arrays;

public class mergeTwoArray {
	public static void main(String[] args) {
		int[] a = {1,2,3,4};
		int[] b = {5,6,7,8};
		
		int[] merged = new int[a.length+b.length];
		System.arraycopy(a, 0, merged, 0, a.length);
		System.arraycopy(b, 0, merged, a.length,b.length);
		System.out.println("Merged Array: "+Arrays.toString(merged));
	}

}
