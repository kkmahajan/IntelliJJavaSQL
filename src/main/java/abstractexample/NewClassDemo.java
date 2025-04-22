package abstractexample;

public class NewClassDemo extends AbstractExample {

    @Override
    public void abstractMethod() {
        System.out.println("Implemented by child class of an abstract class.");
    }

    @Override
    public void nonAbstractMethod() {
        System.out.println("This is printing through overridden method before super");
        super.nonAbstractMethod();
        System.out.println("This is printing through overridden method after Super");
    }
}