import java.util.Scanner;

public class AlienCalendar {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Days of the week
        String[] daysOfWeek = {"Za", "Xo", "Gu", "Va", "Bi", "Ne", "Ma", "Lu", "Ki"};

        // Months and their corresponding days
        String[] months = {"Ra", "Ut", "Ip", "Ok", "Ab", "Ac", "Sy", "Du", "Fi", "Ba", "Gr"};
        int[] daysInMonth = {35, 26, 29, 31, 29, 33, 35, 34, 27, 43, 43};

        // Get month input from user
        System.out.print("Enter month (Ra, Ut, Ip, Ok, Ab, Ac, Sy, Du, Fi, Ba, Gr): ");
        String monthInput = scanner.next();
        scanner.close();

        // Validate month input
        int monthIndex = -1;
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(monthInput)) {
                monthIndex = i;
                break;
            }
        }

        if (monthIndex == -1) {
            System.out.println("Invalid month input. Please enter a valid month.");
            return;
        }

        // Output the calendar for the month
        System.out.println("Month: " + months[monthIndex]);
        for (int day = 1; day <= daysInMonth[monthIndex]; day++) {
            System.out.printf("%2d: %s\n", day, daysOfWeek[(day - 1) % daysOfWeek.length]);
        }
    }
}
