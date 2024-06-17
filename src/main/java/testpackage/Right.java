package testpackage;

public interface Right {

    default void m1(){
        System.out.println("Right Default Method");
    }
}