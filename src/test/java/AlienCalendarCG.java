public class AlienCalendarCG {

    // Map month numbers to their respective days
    private static final int[] monthDays = {36, 29, 26, 29, 31, 29, 33, 34, 34, 35, 43};
    private static final String[] monthNames = {"Ra", "Ta", "Ut", "Ip", "Ok", "Py", "Ar", "Sy", "Du", "Fi", "Gr"};
    private static final String[] weekDays = {"Za", "Xo", "Cu", "Vo", "Bi", "Ne", "Ma", "Lu", "Ki"};

    // Method to print the calendar
    public static void PrintNMonths(int startYear, int startMonth, int numberOfMonths) {
        int currentYear = startYear;
        int currentMonth = startMonth - 1;
        int dayOffset = MonthStartsOn(currentMonth, currentYear);

        for (int i = 0; i < numberOfMonths; i++) {
            printMonthHeader(currentMonth, currentYear);
            printMonthDays(currentMonth, dayOffset, currentYear);

            dayOffset = (dayOffset + getDaysInMonth(currentMonth, currentYear)) % 9;
            currentMonth++;
            if (currentMonth >= 11) {
                currentMonth = 0;
                currentYear++;
            }
        }
    }

    // Method to print month header
    private static void printMonthHeader(int month, int year) {
        System.out.println("+-------------------------------------------+");
        System.out.printf("| %-9s %d |%n", monthNames[month], year);
        System.out.println("+----+----+----+----+----+----+----+----+----+");
        System.out.print("|");
        for (String day : weekDays) {
            System.out.printf(" %-2s |", day);
        }
        System.out.println();
        System.out.println("+----+----+----+----+----+----+----+----+----+");
    }

    // Method to print month days
    private static void printMonthDays(int month, int startDay, int year) {
        int days = getDaysInMonth(month, year);
        int dayCounter = 0;
        System.out.print("|");
        for (int i = 0; i < startDay; i++) {
            System.out.print("    |");
            dayCounter++;
        }
        for (int day = 1; day <= days; day++) {
            System.out.printf(" %2d |", day);
            dayCounter++;
            if (dayCounter % 9 == 0) {
                System.out.println();
                System.out.println("+----+----+----+----+----+----+----+----+----+");
                if (day < days) System.out.print("|");
                dayCounter = 0;
            }
        }
        if (dayCounter > 0) {
            System.out.println();
            System.out.println("+----+----+----+----+----+----+----+----+----+");
        }
    }

    // Method to determine the starting day of the month
    private static int MonthStartsOn(int month, int year) {
        int baseYear = 2600; // Choose a reference year
        int baseDayOffset = 0; // Assume a known start day

        int totalDays = 0;
        for (int y = baseYear; y < year; y++) {
            for (int m = 0; m < 11; m++) {
                totalDays += getDaysInMonth(m, y);
            }
        }
        for (int m = 0; m < month; m++) {
            totalDays += getDaysInMonth(m, year);
        }

        return (baseDayOffset + totalDays) % 9;
    }


    // Adjusts days in Du if year is a multiple of 11 and Ta if total months count is a multiple of 297
    private static int getDaysInMonth(int month, int year) {
        if (month == 8 && year % 11 == 0) { // Du loses a day every 11 years
            return monthDays[month] - 1;
        }
        int totalMonthsSinceYearZero = year * 11 + month; // Each year has 11 months
        if (month == 1 && totalMonthsSinceYearZero % 297 == 0) { // Ta gains a day every 297 months
            return monthDays[month] + 1;
        }
        return monthDays[month];
    }

    public static void main(String[] args) {
        PrintNMonths(2618, 9, 4);
    }
}