package samplecode;

public class StringpROGRM2 {

    public static void main(String[] args) {

        System.out.println(countSequenceChar("aaaabaaaa"));

    }

    public static String countSequenceChar(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        int count = 1;
        char currentChar = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == currentChar) {
                count = count + 1;
            } else {
                str.append(currentChar).append(count);
                currentChar = s.charAt(i);
                count = 1;
            }
        }
        str.append(currentChar).append(count);
        return str.toString();
    }

}
