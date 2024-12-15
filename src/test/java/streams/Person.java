package streams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
    private final String name;
    private final int age;
    private final Gender gender;

    public Person(String name, int age, Gender gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    @Override
    public String toString(){
        return "Person{name = '" + name + "', age = '" + age + "', gender = '" + gender + "'}";
    }
}
