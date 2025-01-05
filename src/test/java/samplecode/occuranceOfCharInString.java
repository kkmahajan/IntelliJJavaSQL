package samplecode;

import java.util.Iterator;

public class occuranceOfCharInString {
	public static void main(String[] args) {
		String str = "automation";
		char ch = 'a';
		int count = 0;
		for(char c:str.toCharArray()) {
			if (ch==c) {
				count++;
			}
		}
		System.out.println("Count is: "+count);
	}

}
