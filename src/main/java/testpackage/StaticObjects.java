package testpackage;

import org.testng.annotations.Test;

public class StaticObjects {

    @Test
    public static int returnInt() {
        return 0;
    }

    @Test
    public static int returnInt(int a) {
        return a;
    }

    @Test
    public static int returnInt(int a, int b) {
        return a + b;
    }
}
