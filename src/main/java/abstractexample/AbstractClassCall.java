package abstractexample;

public class AbstractClassCall {

    public static void main(String[] args) {
        NewClassDemo obj = new NewClassDemo();
        obj.abstractMethod();
        obj.nonAbstractMethod();

        AbstractExample obj1 = new AbstractExample() {
            @Override
            public void abstractMethod() {
                System.out.println("Implemented by a class who is calling an object of an abstract class.");
            }
        };
        obj1.abstractMethod();
        obj1.nonAbstractMethod();

        AbstractExample obj2 = new NewClassDemo();
        obj2.abstractMethod();
        obj2.nonAbstractMethod();

        AbstractExample obj3 = new AbstractExample() {
            @Override
            public void abstractMethod() {
                System.out.println("Implemented by a class who is calling an object of an abstract class.");
            }
        };
        obj3.abstractMethod();
        obj3.nonAbstractMethod();
    }
}
