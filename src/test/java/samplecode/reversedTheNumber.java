package samplecode;

public class reversedTheNumber {
    public static void main(String[] args) {
        int num = 123456;
        int reversed = 0;
        while (num != 0) {
            reversed = reversed * 10 + num % 10;
            System.out.println("Iteration 1 reversed : " + reversed);
            num = num / 10;
            System.out.println("Iteration 1 num : ");
        }
        System.out.println("reversed number is: " + reversed);
    }
}