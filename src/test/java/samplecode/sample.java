package samplecode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class sample {
	public static void main(String[] args) {
	int a = 30;
	int b = 20;
	int c = 40;
	int largest = 0;
	
	if (a>b) {
			if(a>c) {
				largest = a;
			}else {
				largest = c;
			}
		}else if (b>c) {
		largest = b;
		}else {
			largest = c;
		}
	System.out.println("largest number : "+largest);
	
	double radius = Math.PI * a * a;
	System.out.println("radius number : "+radius);
	
	
	int year = 2014;
	
	}
}