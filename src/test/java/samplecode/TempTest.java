package samplecode;

import org.testng.annotations.Test;

public class TempTest {

    /**
     * Prints ASCII values of all printable characters from 32 to 127
     */
    @Test
    public void PrintASCII() {
        for (int i = 32; i <= 127; i++) {
            System.out.println("ASCII value of '" + (char) i + "' is: " + i);
        }
    }

    @Test
    private void testStringIndexOf() {
        String name = "Kaustubh";
        System.out.println(name.indexOf("M")); //returns -1 as M is not present in the name string
        char ch = name.charAt(4);
        char[] charray = name.toCharArray();
        System.out.println(name.indexOf(ch)); // returns 4 as the character is present at location name[1]
        System.out.println(charray[4]);
        System.out.println(charray.length + " " + name.length());
        Character cha;
        cha = ch;
        System.out.println(cha);
        System.out.println("Canonical representation of the String : " + name.intern());
    }
}