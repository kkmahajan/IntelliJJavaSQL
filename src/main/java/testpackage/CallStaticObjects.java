package testpackage;

public class CallStaticObjects {
    public static void main(String[] args) {
        System.out.println(StaticObjects.returnInt());
        System.out.println(StaticObjects.returnInt(1));
        System.out.println(StaticObjects.returnInt(1, 2));
    }
}