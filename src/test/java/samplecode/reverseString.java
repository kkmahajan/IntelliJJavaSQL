package samplecode;

import org.testng.annotations.Test;

public class reverseString {

    public static void main(String[] args) {
        String str = "Automation";
        System.out.println("string: " + str);
        StringBuilder strBuilder = new StringBuilder();
        String New = strBuilder.append(str).reverse().toString();
        System.out.println("reverseString : " + New);

        String input = "hello world";
        String[] b = input.split(" ");
        System.out.println(b.length);

        for (int i = b.length - 1; i >= 0; i--) {
            System.out.println(b[i]);
        }
    }

    @Test
    public void testString() {
        String name = "Kaustubh";
        System.out.println(name.length());
    }
}
