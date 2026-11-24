package samplecode;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TempTest {

    @Test
    public void shouldResolveCharacterPositionsAndStringLength() {
        String name = "Kaustubh";
        char expectedCharacter = 't';

        Assert.assertEquals(name.indexOf("M"), -1);
        Assert.assertEquals(name.indexOf(expectedCharacter), 4);
        Assert.assertEquals(name.charAt(4), expectedCharacter);
        Assert.assertEquals(name.toCharArray()[4], expectedCharacter);
        Assert.assertEquals(name.toCharArray().length, name.length());
        Assert.assertSame(name.intern(), name);
    }

    @Test
    public void shouldMapPrintableAsciiBoundaryValues() {
        Assert.assertEquals((char) 32, ' ');
        Assert.assertEquals((char) 126, '~');
    }
}
