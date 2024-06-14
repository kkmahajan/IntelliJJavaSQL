package javafeatures;

public class Generics<T> {

    T value;

    public Generics(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
