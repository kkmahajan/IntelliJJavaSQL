package streams;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;

public class MainTest {

    @Test
    public void shouldFilterMalesWithoutStreams() {
        List<Person> males = getPeople().stream()
                .filter(person -> person.getGender() == Gender.MALE)
                .toList();

        Assert.assertEquals(males.size(), 5);
        Assert.assertTrue(males.stream().allMatch(person -> person.getGender() == Gender.MALE));
    }

    @Test
    public void shouldApplyCommonStreamOperations() {
        List<Person> people = getPeople();

        List<Person> malesByAge = people.stream()
                .filter(person -> person.getGender() == Gender.MALE)
                .sorted(Comparator.comparing(Person::getAge))
                .toList();

        Assert.assertEquals(malesByAge.stream().map(Person::getAge).toList(), List.of(15, 15, 18, 25, 55));
        Assert.assertTrue(people.stream().allMatch(person -> person.getAge() > 14));
        Assert.assertFalse(people.stream().anyMatch(person -> person.getAge() > 95));
        Assert.assertTrue(people.stream().noneMatch(person -> person.getName().equals("Kaustubh")));
        Assert.assertEquals(people.stream().filter(person -> person.getName().equals("John Watson")).count(), 1L);

        Person oldest = people.stream().max(Comparator.comparing(Person::getAge)).orElseThrow();
        Person youngest = people.stream().min(Comparator.comparing(Person::getAge)).orElseThrow();

        Assert.assertEquals(oldest.getAge(), 55);
        Assert.assertEquals(youngest.getAge(), 15);
    }

    private List<Person> getPeople() {
        return List.of(
                new Person("John Watson", 25, Gender.MALE),
                new Person("Jane Smith", 30, Gender.FEMALE),
                new Person("Bob Johnson", 15, Gender.MALE),
                new Person("Alice Brown", 40, Gender.FEMALE),
                new Person("Erica Webb", 45, Gender.FEMALE),
                new Person("Michael Davis", 18, Gender.MALE),
                new Person("Dean Wilson", 55, Gender.MALE),
                new Person("Dan Wilson", 15, Gender.MALE)
        );
    }
}
