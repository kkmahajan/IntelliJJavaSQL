package abstractexample;

public class NewClassDemo extends AbstractExample {

    @Override
    public void abstractMethod() {
        System.out.println("Implemented by child class of an abstract class.");
    }

    @Override
    public void nonAbstractMethod() {
        super.nonAbstractMethod();
    }
}