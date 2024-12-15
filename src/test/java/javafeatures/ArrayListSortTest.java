package javafeatures;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArrayListSortTest {

    @Test
    public void testSort() {
        ArrayList<String> list = new ArrayList<>();
        list.add("kaustubh");
        list.add("vaibhav");
        list.add("shubham");
        list.add("krishna");
        list.add("ashish");

        System.out.println(list.stream().sorted().toList());
    }

    @Test
    public void testArray() {
        int[] a = {1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Set<Integer> set = new HashSet<>();
        for (int j : a) {
            set.add(j);
        }
        Set<Integer> newSet = new HashSet<>(Arrays.stream(a).boxed().toList());
        System.out.println("For Loop : " + set);
        System.out.println("Stream : " + newSet);
    }

}
