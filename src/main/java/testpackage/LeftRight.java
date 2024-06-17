package testpackage;

public class LeftRight implements Left, Right{

    /**
     * Following method is implemented from two different interfaces where similar method definition is present
     * Line 1 has the class's own implementation of the m1() method
     * Line 2 has the Right interface's implementation of the m1() method
     * Line 3 has the Left interface's implementation of the m1() method
     */
    @Override
    public void m1() {
        System.out.println("Class's own implementation"); // Line 1
        Right.super.m1(); // Line 2
//        Left.super.m1(); // Line 3
    }

    public static void main(String[] args) {
        LeftRight lr = new LeftRight();
        lr.m1();
    }
}
