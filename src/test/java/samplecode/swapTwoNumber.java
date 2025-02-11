package samplecode;

public class swapTwoNumber {
    public static void main(String[] args) {
        int a = 12;
        int b = 13;
        System.out.println("Initial Value of a : " + a + "\nInitial Value of b : " + b);
        a = a + b;
        System.out.println("Value of a after a + b : " + a);
        b = a - b;
        System.out.println("Value of b after subtraction a - b : " + b);
        a = a - b;
        System.out.println("Value of a after a - b : " + a);
        System.out.println("Value of a after swap : " + a + "\nValue of b after swap : " + b);
    }

}
