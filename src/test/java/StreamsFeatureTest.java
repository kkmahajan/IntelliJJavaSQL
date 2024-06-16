import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamsFeatureTest {

    /**
     * Tests various methods of printing and manipulating a list of integers using Streams.
     */
    @Test
    public void testStreams() {

        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        // Basic print without Streams
        System.out.println("Basic Print the list");
        System.out.println(integerList);

        System.out.println("Print using the conventional for loop");
        //Print using conventional for loop
        for (int i = 0; i < integerList.size(); i++) {
            if (integerList.get(i) % 2 == 0) {
                System.out.println("The number " + integerList.get(i) + " is even.");
            }
        }

        System.out.println("Print using enhanced for loop");
        //Print using enhanced for loop
        for (int n : integerList) {
            if (n % 2 == 0) {
                System.out.println("The number " + n + " is even.");
            }
        }

        System.out.println("Print using foreach without lambda function");
        //Print using for each without lambda function
        integerList.forEach(n -> System.out.println(n));

        System.out.println("Print using foreach with lambda function");
        //Print using for each with Lambda function
        integerList.forEach(System.out::println);

        //Print using for each with custom lambda function
        System.out.println("Print numbers + 1 using foreach with customs made lambda function accept with type Integer");
        Consumer<Integer> consumer = new Consumer<>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer + 1);
            }
        };
        integerList.forEach(consumer);

        //Print using Custom lambda function with other type
        System.out.println("print using custom made lambda function");
        Consumer<Integer> consumer1 = System.out::println;
        Consumer<Integer> consumer2 = n -> System.out.println(n + 1);
        integerList.forEach(consumer1);
        integerList.forEach(consumer2);

        // Print using Streams
        System.out.println("Print using Streams and above mentioned foreach lambda function to add + 1");
        Stream<Integer> stream = integerList.stream();
        stream.forEach(consumer2);

        //Print using Streams and filter method of Streams
        System.out.println("Print using Streams along with the defined method only even numbers from the list");
        Stream<Integer> stream2 = integerList.stream();
        Stream<Integer> stream1 = stream2.filter(n -> n % 2 == 0);
        Stream<Integer> stream3 = stream1.map(n -> n * 2);
        stream3.forEach(consumer1);

        // Use Streams with Map, Filter, Reduce
        System.out.println("Print using Map, Filter, Reduce");
        int result = integerList.stream().filter(n -> n % 2 == 0).map(n -> n * 2).reduce(0, Integer::sum);
        System.out.println("Result of the list being filtered with even numbers and \nthen mapped to *2 function and reduced to c + e logic :" + result);
    }
}