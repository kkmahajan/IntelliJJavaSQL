package samplecode;

public class StringProgramTest {
    static String str = "aaaabbbbwwwwddqasaa";

    /**
     * Print the number of consecutive characters in a string.
     */
    public static void main(String[] args) {
        int count;
        for (int i = 0; i < str.length(); i = i + count) {
            count = 1;
            for (int j = i + 1; j < str.length(); j++) {
                if (str.charAt(i) == str.charAt(j)) {
                    count++;
                } else {
                    break;
                }
            }
            System.out.print(String.valueOf(str.charAt(i)) + count);
        }

        System.out.println("\n****Compress String - StringBuilder Method****\n" + compressString(str));
    }

    /**
     * Same code using StringBuilder
     *
     * @param str
     * @return
     */
    public static String compressString(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder compressed = new StringBuilder();
        int count = 1;
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                count++;
            } else {
                compressed.append(str.charAt(i - 1)).append(count);
                count = 1;
            }
        }
        compressed.append(str.charAt(str.length() - 1)).append(count);
        return compressed.toString();
    }
}