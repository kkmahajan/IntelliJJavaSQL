package samplecode;

import java.util.Iterator;

public class primeNumberCheck {
	public static void main(String[] args) {
		int num = 7;
		boolean isPrime = true;
		
		for(int i =2;i<=Math.sqrt(num);i++) {
			if(num%i==0) {
				isPrime= false;
				break;
			}
		}
		System.out.println("status:"+isPrime);
	}

}
