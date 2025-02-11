package samplecode;

public class sample {
    public static void main(String[] args) {
        int a = 30;
        int b = 20;
        int c = 40;
        int largest;

        if (a > b) {
            if (a > c) {
                largest = a;
            } else {
                largest = c;
            }
        } else if (b > c) {
            largest = b;
        } else {
            largest = c;
        }
        System.out.println("largest number : " + largest);

        double radius = Math.PI * a * a;
        System.out.println("radius number : " + radius);


        int year = 2014;

    }
}