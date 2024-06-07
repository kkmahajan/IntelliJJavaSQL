package javafeatures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
    int rollno;
    String name;
    double marks;
    boolean active;

    Student()
    {
        System.out.println("Constructor called without arguments");
    }

    Student(int rollno, String name)
    {
        this.rollno = rollno;
        this.name = name;
        System.out.println("Constructor Called with rollno and name");
    }

    Student(int rollno)
    {
        this.rollno = rollno;
        System.out.println("Constructor Called with rollno only");
    }

    Student(String name)
    {
        this.name = name;
        System.out.println("Constructor Called with name only");

    }

    Student(int rollno, String name, double marks)
    {
        this.rollno = rollno;
        this.name = name;
        this.marks = marks;
        System.out.println("Constructor Called with rollno, name and marks");
    }

    Student(String name, double marks)
    {
        this.name = name;
        this.marks = marks;
        System.out.println("Constructor Called with name and marks");
    }

    Student(double marks)
    {
        this.marks = marks;
        System.out.println("Constructor Called with marks only");
    }
    
    Student(String name, boolean active)
    {
        this.name = name;
        this.active = active;
        System.out.println("Constructor Called with name and active");
    }
    
    Student(boolean active)
    {
        this.active = active;
        System.out.println("Constructor Called with active only");
    }
}
