package samplecode;

import java.util.Arrays;

public class ManipulationArray {
	public static void main(String[] args) {
		int[] a = {1,0,5,-1,2};
		int[] b = new int[5];
		System.out.println(Arrays.toString(a
				));

		for(int i=0;i<a.length;i++) {
			if(i== 0) {
				b[i] = 0+a[i]+a[i+1];
			}else if (i == (a.length-1)) {
				b[i] = a[i-1]+a[i]+0;
			}else {
				b[i] = a[i-1]+a[i]+a[i+1];
			}
			System.out.println(Arrays.toString(b));
		}
		
	}
}