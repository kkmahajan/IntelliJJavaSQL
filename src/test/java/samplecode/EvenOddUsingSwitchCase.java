package samplecode;

import java.util.Scanner;

public class EvenOddUsingSwitchCase {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int num;
        System.out.println("Enter The number: ");
        num = input.nextInt();
        System.out.println("ans num mod 2: " + num % 2);

        switch (num % 2) {
            case 0: {
                System.out.println("Its even number");
                break;
            }
            case 1: {
                System.out.println("its a ODD number");
            }

        }
    }
}


