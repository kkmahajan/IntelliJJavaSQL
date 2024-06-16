package javafeatures;

import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.Test;

@Getter
@Setter
public class Student {
    public int rollno;
    public boolean active;
    private String name;
    private double marks;

    Student() {
        System.out.println("Constructor called without arguments");
    }

    Student(int rollno, String name) {
        this.rollno = rollno;
        this.name = name;
        System.out.println("Constructor Called with rollno and name");
    }

    Student(int rollno) {
        this.rollno = rollno;
        System.out.println("Constructor Called with rollno only");
    }

    Student(String name) {
        this.name = name;
        System.out.println("Constructor Called with name only");

    }

    Student(int rollno, String name, double marks) {
        this.rollno = rollno;
        this.name = name;
        this.marks = marks;
        System.out.println("Constructor Called with rollno, name and marks");
    }

    Student(String name, double marks) {
        this.name = name;
        this.marks = marks;
        System.out.println("Constructor Called with name and marks");
    }

    Student(double marks) {
        this.marks = marks;
        System.out.println("Constructor Called with marks only");
    }

    Student(String name, boolean active) {
        this.name = name;
        this.active = active;
        System.out.println("Constructor Called with name and active");
    }

    Student(boolean active) {
        this.active = active;
        System.out.println("Constructor Called with active only");
    }

    //Constructors with different access modifier
    // Can be used anywhere as it is public
    public Student(boolean a, int s) {
        this.active = a;
        this.rollno = s;
        System.out.println("Constructor Called with active and rollno");
    }

    //Cannot be used outside this class also
    private Student(boolean a, int s, double q) {
        this.active = a;
        this.rollno = s;
        this.marks = q;
        System.out.println("Constructor Called with active, rollno and marks");
    }

    //Can only be used inside the package
    protected Student(boolean a, int s, double q, int w) {
        this.active = a;
        this.marks = q;
        this.rollno = w;
        System.out.println(s + " Constructor Called with active, rollno, marks and rollno");
    }

    //Private constructor can be used inside the class only
    @Test
    public void checkPrivateConstructor() {
        Student s1 = new Student(true, 45, 90.5);
        System.out.println(s1.getRollno());
        System.out.println(s1.getName());
        System.out.println(s1.getMarks());
        System.out.println(s1.isActive());
    }
}