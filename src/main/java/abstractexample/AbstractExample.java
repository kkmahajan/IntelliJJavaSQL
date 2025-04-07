package abstractexample;

public abstract class AbstractExample {

    public static final String CONSTANT = "This is a constant.";

    public abstract void abstractMethod();

    public void nonAbstractMethod() {
        System.out.println("This is a non-abstract method implemented in an Abstract class.");
    }
}