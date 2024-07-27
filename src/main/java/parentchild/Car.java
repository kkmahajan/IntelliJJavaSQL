package parentchild;

public class Car {

    private String carName;
    private int carPrice;

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(int carPrice) {
        this.carPrice = carPrice;
    }

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