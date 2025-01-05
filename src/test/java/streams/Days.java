package streams;

public class Days {
    public static void main(String[] args) {
        for (Day day : Day.values()) {
            System.out.println("Day : " + day + " Name : " + day.name() + " Ordinal : " + day.ordinal());
        }
        System.out.println("Values in Day Enum : " + Day.values().length);
    }
}
