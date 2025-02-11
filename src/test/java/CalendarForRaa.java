static final int[] monthDays = {36, 29, 26, 29, 31, 29, 33, 34, 34, 35, 43};
static final String[] monthNames = {"Ra", "Ta", "Ut", "Ip", "Ok", "Py", "Ar", "Sy", "Du", "Fi", "Gr"};
static final String[] weekDays = {"Za", "Xo", "Cu", "Vo", "Bi", "Ne", "Ma", "Lu", "Ki"};

public static void printNMonths(int startYear, int startMonth, int numberOfMonths) {
    int currentYear = startYear;
    int currentMonth = startMonth - 1;
    int dayOffset = monthStartsOn(currentMonth, currentYear);

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

private static void printMonthHeader(int month, int year) {
    System.out.printf(" %-9s %d %n", monthNames[month], year);
    System.out.println("+----+----+----+----+----+----+----+");
    System.out.print("|");
    for (String day : weekDays) {
        System.out.printf("%-2s|", day);
    }
    System.out.println();
    System.out.println("+----+----+----+----+----+----+----+");
}

private static void printMonthDays(int month, int startDay, int year) {
    int days = getDaysInMonth(month, year);
    int dayCounter = 0;
    System.out.print("|");
    for (int i = 0; i < startDay; i++) {
        System.out.print("|");
        dayCounter++;
    }
    for (int day = 1; day <= days; day++) {
        System.out.printf("%2d|", day);
        dayCounter++;
        if (dayCounter % 9 == 0) {
            System.out.println();
            System.out.println("+----+----+----+----+----+----+----+");
            if (day < days) System.out.print("|");
            dayCounter = 0;
        }
    }
    if (dayCounter > 0) {
        System.out.println();
        System.out.println("=======================================");
    }
}

private static int monthStartsOn(int month, int year) {
    int baseYear = 0;
    int baseDayOffset = 0;

    int totalDays = 0;
    for (int i = baseYear; i < year; i++) {
        for (int j = 0; j < 11; j++) {
            totalDays += getDaysInMonth(j, i);
        }
    }
    for (int i = 0; i < month; i++) {
        totalDays += getDaysInMonth(i, year);
    }
    return (baseDayOffset + totalDays) % 9;
}


private static int getDaysInMonth(int month, int year) {
    if (month == 8 && year % 11 == 0) {
        return monthDays[month] - 1;
    }
    int totalMonthsSinceYearZero = year * 11 + month;
    if (month == 1 && totalMonthsSinceYearZero % 297 == 0) {
        return monthDays[month] + 1;
    }
    return monthDays[month];
}

public static void main(String[] ignoredArgs) {
    printNMonths(2618, 9, 4);
}