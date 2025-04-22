package parentchild;

// Main class demonstrating OOP concepts
public class OOPDemo {

    public static void main(String[] args) {
        // 1. **Inheritance** - BMW is inheriting from Car
        Car genericCar = new Car("Generic Car", 20000);
        BMW bmwCar = new BMW("BMW X5", 60000);

        // 2. **Encapsulation** - Using getter & setter methods
        genericCar.setCarPrice(25000);
        System.out.println("Updated Generic Car Price: " + genericCar.getCarPrice());

        // 3. **Method Overriding** - Calling overridden method in BMW
        genericCar.carSound();  // Calls Car’s method
        bmwCar.carSound();      // Calls overridden BMW’s method

        // 4. **Method Overloading** - Example of static binding
        displayCarInfo(genericCar);
        displayCarInfo(bmwCar, "Luxury Segment");

        // 5. **Polymorphism** - Parent reference pointing to Child object
        Car polymorphicCar = new BMW("BMW 7 Series", 85000);
        polymorphicCar.carSound(); // Calls BMW’s overridden method (Dynamic Binding)

        // 6. **Binding** - Static and Dynamic Binding
        staticBindingExample(); // Static binding (Compile-time)
        polymorphicCar.displayCarPrice(); // Dynamic binding (Run-time)

        // 7. **Abstraction (through method overriding)** - Parent defines a method, child modifies behavior
        BMW specificBMW = new BMW("BMW M3", 70000);
        specificBMW.displayCarPrice();
    }

    // **Method Overloading** (Compile-time Polymorphism)
    public static void displayCarInfo(Car car) {
        System.out.println("Car Info: " + car.getCarName() + ", Price: $" + car.getCarPrice());
    }

    public static void displayCarInfo(BMW car, String category) {
        System.out.println("BMW Info: " + car.getCarName() + " (" + category + "), Price: $" + car.getCarPrice());
    }

    // **Static Binding Example**
    public static void staticBindingExample() {
        System.out.println("This is a method with static binding (Compile-time resolution)");
    }
}
