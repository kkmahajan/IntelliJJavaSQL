package samplecode;

import java.util.Iterator;

public class StringProgram {
	public static void main(String[] args) {
		String str = "aabbba";
		
		int count = 1;
		for (int i=0;i<str.length();i=i+count) {
			count = 1;
			for(int j = i+1;j<str.length();j++) {
				if(str.charAt(i)==str.charAt(j)) {
					count = count+1;
				}else {
					break;
				}
			}
			System.out.print(str.charAt(i)+ ""+ count);
		}
	}
	
//	String method(String input) {
//		int count = 1;
//		String result = "";
//		for(int i=0;i<input.length();i++) {
//			for(int j=i+1;j<input.length();j++) {
//				if(input.charAt(i)==input.charAt(j)) {
//					count = count+1;
//				}else {
//					break;
//				}
//				result =result +""+input.charAt(i)+""+count; 
//			}
//		}
//		return result;
//	}
	
//	int x=1;
//	static int y=2;
//	  public static void main(String[] args) {
//		  StringProgram m=new StringProgram();
//	  m.x=3;
//	  m.y=4; 
//	  StringProgram m1=new StringProgram();
//	  System.out.println("x:"+m.x);
//	  System.out.println("x:"+m.y);
//	     System.out.println("x:"+m1.x);
//	    System.out.println("x:"+m1.y);
//	  }
}

