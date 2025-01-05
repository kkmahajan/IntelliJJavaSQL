package samplecode;

public class reversedTheNumber {
	public static void main(String[] args) {
		int num = 123456;
		int reversed = 0;
		while(num!=0) {
			reversed = reversed * 10 +num%10;
			num = num/10;
		}
		System.out.println("reversed number is: "+reversed);
	}

}
