package parentchild;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
        System.out.println("Price of the car is: " + carPrice);
    }
}