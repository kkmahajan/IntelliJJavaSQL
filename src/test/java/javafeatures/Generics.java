package javafeatures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Generics<T> {

    T value;

    //Constructor with value
    public Generics(T value) {
        this.value = value;
    }
}