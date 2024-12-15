package streams;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainTest {

    @Test
    public static void methodWithoutStreams() {
        List<Person> people = getPeople();

        List<Person> males = new ArrayList<>();
        for (Person person : people) {
            if (person.getGender().equals(Gender.MALE)) {
                males.add(person);
            }
        }
        males.forEach(System.out::println);
    }

    @Test
    public static void methodWithStreams() {
        List<Person> people = getPeople();
//        System.out.println("\n--ALL PERSONS--\n");
//        people.forEach(System.out::println);

//        System.out.println("\n--FILTERED ONLY MALES FROM PERSONS--\n");
        //Using streams to get all Person as Males
        List<Person> males = people.stream().filter(person -> person.getGender().equals(Gender.MALE)).toList();
//        males.forEach(System.out::println);

//        System.out.println("\n--SORTED ACCORDING TO AGE ASCENDING--\n");

        //Using streams to get all Person males sorted
        List<Person> malesSorted = males.stream().sorted(Comparator.comparing(Person::getAge)).toList();
//        malesSorted.forEach(System.out::println);

//        System.out.println("\n--SORTED ACCORDING TO AGE DESCENDING--\n");
        List<Person> malesSortedDesc = males.stream().sorted(Comparator.comparing(Person::getAge).reversed()).toList();
//        malesSortedDesc.forEach(System.out::println);

//        System.out.println("\n--SORTED ACCORDING TO AGE ASCENDING AND FEMALE--\n");
        List<Person> sorted = people.stream().sorted(Comparator.comparing(Person::getAge).thenComparing(Person::getGender)).toList();
//        sorted.forEach(System.out::println);

//        System.out.println("\n--SORTED ACCORDING TO NAME ASCENDING--\n");
        List<Person> malesSortedName = males.stream().sorted(Comparator.comparing(Person::getName)).toList();
//        malesSortedName.forEach(System.out::println);

        System.out.println("\n--ARE ALL [ALL MATCH] PERSONS WITH AGE GREATER THAN 14--\n");
        boolean isPresent = people.stream().allMatch(person -> person.getAge() > 14);
        System.out.println("Are all persons present with age more than 14 : " + isPresent);

        System.out.println("\n--ARE ANY [ANY MATCH] PERSONS WITH AGE GREATER THAN 14--\n");
        boolean isPresent2 = people.stream()
                .anyMatch(person -> person.getAge() > 95);
        System.out.println("Any person present with age more than 95 : " + isPresent2);

        System.out.println("\n--ARE NONE [NONE MATCH] PERSONS WITH NAME AS Kaustubh--\n");
        boolean isPresent3 = people.stream()
                .noneMatch(person -> person.getName()
                .equals("Kaustubh"));
        System.out.println("No person present with name as Kaustubh : " + isPresent3);

        System.out.println("\n--COUNT OF PERSONS WITH NAME AS John Watson--\n");
        long count = people.stream()
                .filter(person -> person.getName()
                .equals("John Watson"))
                .count();
        System.out.println("\n--Count of John Watson : " + count);

        System.out.println("\n--COUNT OF MALES WITH NAME AS John Watson--\n");
        long count2 = people.stream()
                .filter(person -> person.getGender().equals(Gender.MALE))
                .filter(person -> person.getName()
                .equals("John Watson"))
                .count();
        System.out.println("\n--Count of John Watson : " + count2);

        System.out.println("\n--FIND THE PERSON WITH MAXIMUM AGE--\n");
        Person maxAgePerson = people.stream()
                .max(Comparator.comparing(Person::getAge))
                .orElse(null);
        System.out.println("Person with maximum age : " + maxAgePerson);

        System.out.println("\n--FIND THE PERSON WITH MINIMUM AGE--\n");
        Person minAgePerson = people.stream()
                .min(Comparator.comparing(Person::getAge))
                .orElse(null);
        System.out.println("Person with minimum age : " + minAgePerson);

        
    }

    private static List<Person> getPeople() {
        return List.of(new Person("John Watson", 25, Gender.MALE), new Person("Jane Smith", 30, Gender.FEMALE), new Person("Bob Johnson", 15, Gender.MALE), new Person("Alice Brown", 40, Gender.FEMALE), new Person("Erica Webb", 45, Gender.FEMALE), new Person("Michael Davis", 18, Gender.MALE), new Person("Dean Wilson", 55, Gender.MALE), new Person("Dan Wilson", 15, Gender.MALE));
    }
}
