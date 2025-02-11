package javafeatures;

import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

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

    @Test
    public void testArray2() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        List<Integer> squaredNumbers = numbers.stream().map(n -> n * n).collect(Collectors.toList());
        System.out.println(squaredNumbers); // [1, 4, 9, 16]
    }

    @Test
    public void testArray3() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        int sum = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println(sum); // 15
    }

    @Test
    public void testArray4() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println(sum); // 15
    }

    @Test
    public void testJoin() {
        List<String> names = Arrays.asList("John", "Jane", "Jack");
        String result = names.stream().collect(Collectors.joining(", "));
        System.out.println(result); // "John, Jane, Jack"
    }

    @Test
    public void testJoin2() {
        List<String> names = Arrays.asList("John", "Jane", "Jack");
        String result = String.join(", ", names);
        System.out.println(result); // "John, Jane, Jack"
    }

    @Test
    public void testJoin3() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.parallelStream().forEach(n -> System.out.println(n + " " + Thread.currentThread().getName()));
    }
}