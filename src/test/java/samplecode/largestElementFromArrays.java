package samplecode;

public class largestElementFromArrays {
	public static void main(String[] args) {
		int[] a = {1,2,3,4,5,6};
		int largest = a[0];
		for(int n:a) {
			System.out.println("value of n: "+n);
			if(n>largest) {
				
				largest = n;
			}
		}
		System.out.println("largest number from arrays: "+largest);
	}

}
