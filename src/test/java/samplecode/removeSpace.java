package samplecode;

public class removeSpace {
	public static void main(String[] args) {
		String str = "A U T O M A T I O N";
		String result = str.replaceAll(" ", "");
		System.out.println("result is: "+result);
	}

}
