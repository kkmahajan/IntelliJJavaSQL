package javafeatures;

import org.testng.annotations.Test;

public class ConstructorExample {
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
        System.out.println("S1 Student Roll No : " + s1.getRollno());
        System.out.println("S1 Student Name : " + s1.getName());
        System.out.println("S1 Student Marks : " + s1.getMarks());
        System.out.println();

        Student s2 = new Student(20, "Jack");
        System.out.println("S2 Student Roll No : " + s2.getRollno());
        System.out.println("S2 Student Name : " + s2.getName());
        System.out.println("S2 Student Marks : " + s2.getMarks());
        System.out.println();

        Student s3 = new Student(30);
        System.out.println("S3 Student Roll No : " + s3.getRollno());
        System.out.println("S3 Student Name : " + s3.getName());
        System.out.println("S3 Student Marks : " + s3.getMarks());
        System.out.println();

        Student s4 = new Student("Harry");
        System.out.println("S4 Student Roll No : " + s4.getRollno());
        System.out.println("S4 Student Name : " + s4.getName());
        System.out.println("S4 Student Marks : " + s4.getMarks());
        System.out.println();

        Student s5 = new Student(20, "Jack", 90.5);
        System.out.println("S5 Student Roll No : " + s5.getRollno());
        System.out.println("S5 Student Name : " + s5.getName());
        System.out.println("S5 Student Marks : " + s5.getMarks());
        System.out.println();

        Student s6 = new Student("Harry", 90.5);
        System.out.println("S6 Student Roll No : " + s6.getRollno());
        System.out.println("S6 Student Name : " + s6.getName());
        System.out.println("S6 Student Marks : " + s6.getMarks());
        System.out.println();

        Student s7 = new Student(90.5);
        System.out.println("S7 Student Roll No : " + s7.getRollno());
        System.out.println("S7 Student Name : " + s7.getName());
        System.out.println("S7 Student Marks : " + s7.getMarks());
        System.out.println("S7 Student Activity : " + s7.isActive());
        System.out.println();

        Student s8 = new Student(true);
        System.out.println("S8 Student Roll No : " + s8.getRollno());
        System.out.println("S8 Student Name : " + s8.getName());
        System.out.println("S8 Student Marks : " + s8.getMarks());
        System.out.println("S8 Student Activity : " + s8.isActive());
        System.out.println();
    }
}
