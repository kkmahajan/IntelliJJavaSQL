package pubmatic;

public class ReverseStringIgnoreSpecialCharsTest {

    public static void main(String[] args) {
        String str = "A/B$C";
        String result = reversePreservingSpecialCharacters(str);
        System.out.println("Reversed string: " + result); // Output: C/B$A
    }

    public static String reversePreservingSpecialCharacters(String str) {
        // Convert the string into a character array
        char[] chars = str.toCharArray();

        // Initialize two pointers: one at the beginning, one at the end
        int left = 0, right = chars.length - 1;

        while (left < right) {
            // Skip special characters at the left pointer
            if (!Character.isLetterOrDigit(chars[left])) {
                left++;
            }
            // Skip special characters at the right pointer
            else if (!Character.isLetterOrDigit(chars[right])) {
                right--;
            }
            // Swap the characters if both are alphanumeric
            else {
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;
                left++;
                right--;
            }
        }

        // Convert the character array back to a string and return
        return new String(chars);
    }
}
