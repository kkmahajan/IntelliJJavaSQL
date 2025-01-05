package samplecode;

public class findVowelsConsonants {
	public static void main(String[] args) {
		String str = "abcdefghijklmnopqrstuvwxyz";
		int Vowels = 0;
		int Consonants = 0;
		for(char c:str.toCharArray()) {
			if("aeiouAEIOU".indexOf(c)!= -1) {
				Vowels ++;
			}else if (Character.isLetter(c)) {
				Consonants ++;
			}
		}
		System.out.println("vowels: "+Vowels+" & consonants :"+Consonants);
	}

}
