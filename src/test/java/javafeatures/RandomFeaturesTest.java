package javafeatures;

import org.apache.commons.lang3.ObjectUtils;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class RandomFeaturesTest {

    @Test
    public void testStringDeclarations() {

        //Ways to create String variables
        //Using String Literals:
        String s1 = "Hello";

        //Using the new keyword:
        String s2 = "Hello";

        //Using a character array
        char[] charArray = {'H', 'e', 'l', 'l', 'o'};
        String str3 = new String(charArray);

        //This prints false
        System.out.println(s1 == str3);

        //This prints true
        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));
        System.out.println(s1.equals(str3));


        //Using a byte array
        byte[] byteArray = {72, 101, 108, 108, 111}; // ASCII values of 'Hello'
        String str4 = new String(byteArray); //
        String str5 = new String(byteArray, 1, 3); // "ell" from the Hello string

        //Using String.valueOf()
        int num = 123;
        String str8 = String.valueOf(num);

        //Using String.format()
        String str9 = String.format("%s, %s!", "Hello", "World");

        //Using String.join()
        String[] array = {"Hello", "World"};
        String str10 = String.join(", ", array);

        //Using String.concat()
        String str11 = "Hello".concat(", World!");

        //Using String.copyValueOf()
        char[] charArray2 = {'H', 'e', 'l', 'l', 'o'};
        String str12 = String.copyValueOf(charArray2);
    }

    @Test
    public void removeDuplicateCharsFromString() {
        String s = "Kaustubh";
        HashSet<Character> set = new HashSet<>();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if (!set.contains(s.charAt(i))) {
                set.add(s.charAt(i));
                sb.append(s.charAt(i));
            }
        }
        System.out.println(sb);
    }

    @Test
    public void reverseString() {
        String s = "Kaustubh";
        StringBuffer sb = new StringBuffer();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        System.out.println(sb);
    }

    @Test
    public void reverseStringArray() {
        String s = "Kaustubh is an Automation Engineer";
        String[] splitString = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = splitString.length - 1; i >= 0; i--) {
            sb.append(splitString[i]).append(" ");
            System.out.println(splitString[i]);
        }
        System.out.println("Original String : " + s);
        System.out.println("Reversed String : " + sb);
    }

    /**
     * String: Immutable, suitable for fixed or infrequently changing text, not thread-safe.
     * <p>
     * StringBuffer: Mutable, thread-safe, suitable for multithreaded environments,
     * has synchronization overhead.
     * <p>
     * StringBuilder: Mutable, not thread-safe, suitable for single-threaded environments,
     * better performance than StringBuffer in non-threaded contexts.
     */
    @Test
    public void stringBuffer() {
        String n1 = "Kaustubh";
        n1 = n1.concat(" Mahajan");
        System.out.println(n1);

        StringBuilder sbldr = new StringBuilder("Kaustubh");
        sbldr.append(" Mahajan");
        // Actual string will change here unlike above String
        // In above string we have assign the new value to the String variable again
        System.out.println(sbldr);
        System.out.println(sbldr.capacity());
        sbldr.ensureCapacity(50); // Increase the memory allocation for the string buffer
        System.out.println(sbldr.capacity());
        sbldr.insert(0, "Shruti ");
        System.out.println(sbldr);
        sbldr.reverse();
        System.out.println(sbldr);
        sbldr.reverse();
        System.out.println(sbldr);
        System.out.println(sbldr.capacity());
        sbldr.append("This is a test string");
        System.out.println(sbldr);
        sbldr.delete(sbldr.indexOf("This"), sbldr.length());
        System.out.println(sbldr);


        StringBuffer sb = new StringBuffer("Kaustubh");
        sb.append(" Mahajan");
        // Actual string will change here unlike above String
        // In above string we have assign the new value to the String variable again
        System.out.println(sb);
        System.out.println(sb.capacity());
        sb.ensureCapacity(50); // Increase the memory allocation for the string buffer
        System.out.println(sb.capacity());
        sb.insert(0, "Shruti ");
        System.out.println(sb);
        sb.reverse();
        System.out.println(sb);
        sb.reverse();
        System.out.println(sb);
        System.out.println(sb.capacity());
        sb.append("This is a test string");
        System.out.println(sb);
        sb.delete(sb.indexOf("This"), sb.length());
        System.out.println(sb);
        sb.setLength(100);
        System.out.println(sb);
    }

    @Test
    public void testStringFormat(){
        String str9 = String.format("Hi this is %s from %s of Java", "Kaustubh", "World");
        System.out.println(str9);
    }

    @Test
    public void testStringArray(){
        String a[] = {"Kaustubh", "Mahajan"};
        System.out.println(a[0]);
        System.out.println(a[1]);

        String[] ab = {"Kaustubh", "Mahajan"};
        System.out.println(ab[0]);
        System.out.println(ab[1]);

        String[] abc = new String[] {"Kaustubh", "Mahajan"};
        System.out.println(abc[0]);
        System.out.println(abc[1]);
        System.out.println(ab == abc);
        System.out.println(a==ab);
    }

    @Test
    public void testStringBufferArray(){
        StringBuffer[] a = new StringBuffer[]{new StringBuffer("Kaustubh"), new StringBuffer("Mahajan")};
        System.out.println(a[0]);
        System.out.println(a[1]);
    }

    /**
     * This is an example of a <CheckedException> where the program throws an exception
     * and java has already told us to deal it before compiling the code.
     * The exceptions which are found or checked at the compile time are known as <CheckedException>
     */
    @Test
    public void testExceptionHandling(){
        try{
            System.out.println("\nIn try block before exception");
//            System.out.println("\n"+10/0);
            System.out.println(10/2);
            System.out.println("In try block after exception");
        }
        catch(ArithmeticException e){
            System.out.println("\nArithmeticException Object");
            System.out.println("\n"+e);
            System.out.println("\nStackTrace");
            e.printStackTrace();
            System.out.println("\nGet Cause");
            e.getCause();
            System.out.println("\nIn catch block");
        }finally {
            System.out.println("\nIn finally block");
        }
    }

    @Test
    public void testEmptyCatch(){
        try {
            System.out.println(10 / 0);
        } catch (ArithmeticException e) {
            //Empty catch block
        }
        finally {
            System.out.println("In finally block");
        }
    }

    /**
     * This is an example of an <UncheckedException>
     * Here java will not know that the exception will occur or not.
     * The exceptions which are thrown at run time are known as <UncheckedException>
     */
    @Test
    public void uncheckedExceptionTest(){
        String s = null;
        // We will surely get a FileNotFoundException as there is not file specified
        // But we will also get a NullPointerException which will occur due to null String passed to the FileReader
        // Hence we will get an UncheckedException
        try {
            FileReader fileReader = new FileReader(s);
            fileReader.read();
        }
        catch (NullPointerException | IOException ioe) {
            System.out.println("Null Pointer Exception caused which was caught");
        }
        finally {
            String str = "Hello";
            // We will surely get a FileNotFoundException as there is not file specified
            // But we will not get a NullPointerException here as we are not passing null to the FileReader
            // Hence we will get a CheckedException
            try {
                System.out.println("Inside try block of finally block");
                FileReader fileReader = new FileReader(str);
                String readFile = String.valueOf(fileReader.read());
                System.out.println(readFile);
                fileReader.close();
            }
            catch (IOException e) {
                System.out.println("Caught Exception in catch block of finally block");
            }finally {
                System.out.println("inside finally block of finally block");
            }
        }
    }

    @Test
    public void testIndexOutOfBoundException(){
        String s = "Hello";
        try {
            System.out.println(s.charAt(10));
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Caught StringIndexOutOfBoundsException");
        }finally {
            System.out.println("Always verify the length of the string variables");
        }
        int[] a = {1, 2, 3};
        try {
            System.out.println(a[10]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught ArrayIndexOutOfBoundsException");
        }finally {
            System.out.println("Always verify the length of the array variables");
        }
    }
}
