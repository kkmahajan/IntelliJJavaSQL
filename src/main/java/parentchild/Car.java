package parentchild;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Car {

    private String carName;
    private int carPrice;

    public Car(String carName, int carPrice) {
        this.carName = carName;
        this.carPrice = carPrice;
    }

    public void carSound() {
        System.out.println("Generic Car sound");
    }

    public void displayCarPrice() {
        System.out.println("Car Method : Price of the car is: " + carPrice);
    }
}