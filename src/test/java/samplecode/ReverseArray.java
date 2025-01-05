package samplecode;

public class ReverseArray {
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 3, 4};

        // Loop from the last index to the first index
        for (int n = a.length - 1; n >= 0; n--) {
            System.out.print(a[n] + ", ");
        }
    }

}
