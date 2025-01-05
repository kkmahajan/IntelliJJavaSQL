package samplecode;

public class checkIfStringEmpty {
    public static void main(String[] args) {
        String str = " "; // Blank String
        String str1 = ""; // Empty String
        if (str.isEmpty()) {
            System.out.println("String is empty.");
        } else if (str.isBlank()) {
            System.out.println("String is Blank.");
        }
    }

}
