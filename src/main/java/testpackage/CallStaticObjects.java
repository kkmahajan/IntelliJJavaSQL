package testpackage;

public class CallStaticObjects {
    static {
        System.out.println(StaticObjects.returnInt(1, 2));
        System.out.println("This is called from static block");
    }

    public static void main(String[] args) {
        System.out.println(StaticObjects.returnInt());
        System.out.println(StaticObjects.returnInt(1));
        System.out.println(StaticObjects.returnInt(1, 2));
        System.out.println("this is called from main method");
    }
}