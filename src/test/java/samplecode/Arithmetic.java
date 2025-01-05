package samplecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map.Entry;

public class Arithmetic {
    private static final Logger log = LoggerFactory.getLogger(Arithmetic.class);

    public static void main(String[] args) {

        HashMap<String, String> AB = new HashMap<>();
        AB.put("name", "Bhavik");
        AB.put("name1", "Rushikesh");
        AB.put("name2", "Sanket");

        for (Entry<String, String> entry : AB.entrySet()) {
            System.out.println("key : " + entry.getKey() + ", value: " + entry.getValue());
        }

        try {
            int a = 100 / 0;
        } catch (ArithmeticException arithmeticException) {
            log.error("The above operation caused this exception : ", arithmeticException);
        }
    }
}
