package samplecode;

import java.util.Random;

public class generateRandonNumber {
	public static void main(String[] args) {
		Random randam = new Random();
		int s = randam.nextInt(10000);
		System.out.println("random number is: "+s);
	}

}
