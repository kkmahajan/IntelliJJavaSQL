package parentchild;

public class BMW extends Car {

    public BMW(String carName, int carPrice) {
        super(carName, carPrice);
    }

    @Override
    public void carSound() {
        System.out.println("BMW Car sound");
    }

    public void displayCarPriceInDollars() {
        System.out.println("BMW Method : Price of the car is: " + getCarPrice() + " dollars");
    }
}
