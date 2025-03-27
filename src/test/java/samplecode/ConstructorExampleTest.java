package samplecode;

import org.testng.annotations.Test;

public class ConstructorExampleTest {
    @Test
    public void constructorTest() {

        /*
         *  Default Values for -
         *  String = null
         *  int  = 0
         *  boolean = false
         *  double = 0.0
         */

        Student s1 = new Student();
        System.out.println("S1 samplecode.Student Roll No : " + s1.getRollno());
        System.out.println("S1 samplecode.Student Name : " + s1.getName());
        System.out.println("S1 samplecode.Student Marks : " + s1.getMarks());
        System.out.println();

        Student s2 = new Student(20, "Jack");
        System.out.println("S2 samplecode.Student Roll No : " + s2.getRollno());
        System.out.println("S2 samplecode.Student Name : " + s2.getName());
        System.out.println("S2 samplecode.Student Marks : " + s2.getMarks());
        System.out.println();

        Student s3 = new Student(30);
        System.out.println("S3 samplecode.Student Roll No : " + s3.getRollno());
        System.out.println("S3 samplecode.Student Name : " + s3.getName());
        System.out.println("S3 samplecode.Student Marks : " + s3.getMarks());
        System.out.println();

        Student s4 = new Student("Harry");
        System.out.println("S4 samplecode.Student Roll No : " + s4.getRollno());
        System.out.println("S4 samplecode.Student Name : " + s4.getName());
        System.out.println("S4 samplecode.Student Marks : " + s4.getMarks());
        System.out.println();

        Student s5 = new Student(20, "Jack", 90.5);
        System.out.println("S5 samplecode.Student Roll No : " + s5.getRollno());
        System.out.println("S5 samplecode.Student Name : " + s5.getName());
        System.out.println("S5 samplecode.Student Marks : " + s5.getMarks());
        System.out.println();

        Student s6 = new Student("Harry", 90.5);
        System.out.println("S6 samplecode.Student Roll No : " + s6.getRollno());
        System.out.println("S6 samplecode.Student Name : " + s6.getName());
        System.out.println("S6 samplecode.Student Marks : " + s6.getMarks());
        System.out.println();

        Student s7 = new Student(90.5);
        System.out.println("S7 samplecode.Student Roll No : " + s7.getRollno());
        System.out.println("S7 samplecode.Student Name : " + s7.getName());
        System.out.println("S7 samplecode.Student Marks : " + s7.getMarks());
        System.out.println("S7 samplecode.Student Activity : " + s7.isActive());
        System.out.println();

        Student s8 = new Student(true);
        System.out.println("S8 samplecode.Student Roll No : " + s8.getRollno());
        System.out.println("S8 samplecode.Student Name : " + s8.getName());
        System.out.println("S8 samplecode.Student Marks : " + s8.getMarks());
        System.out.println("S8 samplecode.Student Activity : " + s8.isActive());
        System.out.println();

        Student s9 = new Student("Harry", true);
        System.out.println("S9 samplecode.Student Roll No : " + s9.getRollno());
        System.out.println("S9 samplecode.Student Name : " + s9.getName());
        System.out.println("S9 samplecode.Student Marks : " + s9.getMarks());
        System.out.println("S9 samplecode.Student Activity : " + s9.isActive());
        System.out.println();

        //Protected constructor
        Student s10 = new Student(true, 45, 45.4, 56);
        System.out.println("S10 samplecode.Student Roll No : " + s10.getRollno());
        System.out.println("S10 samplecode.Student Name : " + s10.getName());
        System.out.println("S10 samplecode.Student Marks : " + s10.getMarks());
        System.out.println("S10 samplecode.Student Activity : " + s10.isActive());

        //Private constructor cannot be called
//        samplecode.Student s11 = new samplecode.Student(true, 45, 45.4);
//        System.out.println("S11 samplecode.Student Roll No : " + s11.getRollno());
//        System.out.println("S11 samplecode.Student Name : " + s11.getName());
//        System.out.println("S11 samplecode.Student Marks : " + s11.getMarks());
//        System.out.println("S11 samplecode.Student Activity : " + s11.isActive());

        //Public Constructor
        Student s12 = new Student(true, 45);
        System.out.println("S12 samplecode.Student Roll No : " + s12.getRollno());
        System.out.println("S12 samplecode.Student Name : " + s12.getName());
        System.out.println("S12 samplecode.Student Marks : " + s12.getMarks());
        System.out.println("S12 samplecode.Student Activity : " + s12.isActive());

    }
}
