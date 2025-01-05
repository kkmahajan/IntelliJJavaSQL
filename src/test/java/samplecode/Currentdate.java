package samplecode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Currentdate {

    public static void main(String[] args) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String formattedCurrentDate = currentDate.format(formatter);
        System.out.println("date: " + formattedCurrentDate);

        System.out.println("month: " + formattedCurrentDate.substring(3, 5));

        System.out.println("year: " + formattedCurrentDate.substring(6, 8));

        System.out.println("day: " + formattedCurrentDate.substring(0, 2));
    }
}
