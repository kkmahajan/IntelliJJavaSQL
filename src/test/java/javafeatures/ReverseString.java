package javafeatures;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ReverseString {

    @Test
    public void shouldReverseString() {
        Assert.assertEquals(reverse("Hello, World!"), "!dlroW ,olleH");
        Assert.assertEquals(reverse(""), "");
    }

    @Test
    public void shouldApplyBitwiseComplement() {
        Assert.assertEquals(~(-100), 99);
    }

    private String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }
}
