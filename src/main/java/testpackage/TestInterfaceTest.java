package testpackage;

import org.testng.annotations.Test;

@FunctionalInterface
interface TestInterface {
    void calc(int a, int b);
}

public class TestInterfaceTest {

    int x = 10;

    @Test
    public void m2() {
        int y = 20;
        TestInterface ti = (a, b) -> {
            System.out.println(a + b);
        };
        ti.calc(12, 12);
    }

    @Test
    public void m3() {
        TestInterface ti = (x, y) -> System.out.println(x * y);
        ti.calc(2, 5);
    }
}
