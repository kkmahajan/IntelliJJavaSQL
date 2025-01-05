package parentchild;

import org.testng.annotations.Test;

public class MyNewClassTest {

    Car car = new BMW("BMW", 100000);
//    BMW bmw = (BMW) new Car("BMW", 100000); //WRONG IMPLEMENTATION
    Car audi = new Car("Audi", 102000);
    BMW bmw = new BMW("BMW1", 120000);

    @Test
    public void testCar() {
        car.carSound();
        System.out.println(car.getCarName());
        car.displayCarPrice();
        car.setCarPrice(200000);
        car.displayCarPrice();
        bmw.displayCarPriceInDollars();
        audi.carSound();
        audi.displayCarPrice();
        audi.setCarPrice(200000);
        audi.displayCarPrice();
    }
}
