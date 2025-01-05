package javafeatures;

import org.testng.annotations.Test;

import java.util.Arrays;

public class PrintTest {

    @Test
    public void testPrintOnlyOnce(){
        int[] a = {1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // Use Streams to remove duplicates and print unique integers
        System.out.println("Unique integers:");
        Arrays.stream(a)
                .distinct() // Remove duplicates
                .forEach(System.out::println); // Print each unique integer
    }
}