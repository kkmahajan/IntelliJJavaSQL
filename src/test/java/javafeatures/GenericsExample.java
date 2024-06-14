package javafeatures;

import java.util.ArrayList;

public class GenericsExample {
    public static void main(String[] args) {
        Generics<String> generics = new Generics<>("Hello");

        System.out.println(generics.getValue());
        System.out.println(generics.getValue().getClass().getName());

        Generics<Integer> generics2 = new Generics<>(1234); // <Integer>

        System.out.println(generics2.getValue());
        System.out.println(generics2.getValue().getClass().getName());

        Generics<ArrayList<Integer>> generics3 = new Generics<>(new ArrayList<>());
        generics3.getValue().add(10);
        generics3.getValue().add(20);

        System.out.println(generics3.getValue());
        System.out.println(generics3.getValue().getClass().getName());
    }
}
