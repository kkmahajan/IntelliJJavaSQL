package javafeatures;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class RemoveDuplicatesTest {

    @Test
    public void shouldRemoveDuplicateCharactersAndPreserveOrder() {
        Assert.assertEquals(removeDuplicates("Capgemini"), "Capgemin");
    }

    @Test
    public void shouldReverseWordOrder() {
        Assert.assertEquals(reverseWords("i love java programming"), "programming java love i");
    }

    private String removeDuplicates(String input) {
        Set<Character> seen = new LinkedHashSet<>();
        StringBuilder result = new StringBuilder();

        for (char character : input.toCharArray()) {
            if (seen.add(character)) {
                result.append(character);
            }
        }
        return result.toString();
    }

    private String reverseWords(String input) {
        String[] words = input.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(words[i]);
        }
        return result.toString();
    }
}
