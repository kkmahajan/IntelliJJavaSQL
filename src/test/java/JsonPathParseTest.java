import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.example.TestData.COURSE_JSON_FILE_PATH;

public class JsonPathParseTest {

    // As API is not yet available, mocked response can be used to complete automation for such APIs
    JsonPath jsonPath = new JsonPath(Payload.coursePrice());

    // Print no of courses returned by API
    @Test
    public void printNoOfCourses() {
        // .size() can be applied only to List & Json Arrays
        System.out.println("No of Courses in the Response : " + jsonPath.getInt("courses.size()"));
        System.out.println(jsonPath.getString("dashboard"));
        System.out.println(jsonPath.getInt("dashboard.size()"));
    }

    @Test
    public void printPurchaseAmountAsInt() {
        System.out.println("The Purchase amount is : " + jsonPath.getInt("dashboard.purchaseAmount"));
    }

    @Test
    public void printPurchaseAmountAsString() {
        System.out.println("The Purchase amount is : " + jsonPath.getString("dashboard.purchaseAmount"));
    }

    @Test
    public void getFirstCourseTitle() {
        System.out.println("The first course title is : " + jsonPath.getString("courses[0].title"));
    }

    @Test
    public void printAllCoursesAndTheirPrice() {
        for (int i = 0; i < jsonPath.getInt("courses.size()"); i++) {
            System.out.println("Course " + (i + 1) + " :" + jsonPath.getString("courses.title[" + i + "]"));
            System.out.println("Price of Course " + (i + 1) + " :" + jsonPath.getString("courses.price[" + i + "]"));
        }
    }

    @Test
    public void printCopiesSoldByRPACourse() {
        for (int i = 0; i < jsonPath.getInt("courses.size()"); i++) {
            String courseTitles = jsonPath.get("courses.title[" + i + "]");
            if (courseTitles.equalsIgnoreCase("RPA")) {
                System.out.println("The number of copies sold for RPA : " + jsonPath.getString("courses.copies[" + i + "]"));
                break;
            }
        }
    }

    @Test
    public void verifyAllThePricesAddUpToTheTotalAmount() {
        int priceAmount = 0;
        for (int i = 0; i < jsonPath.getInt("courses.size()"); i++) {
            int copies = jsonPath.getInt("courses.copies[" + i + "]");
            int price = jsonPath.getInt("courses.price[" + i + "]");
            priceAmount = priceAmount + (copies * price);
        }
        int totalPriceAmountInResponse = jsonPath.getInt("dashboard.purchaseAmount");
        Assert.assertEquals(priceAmount, totalPriceAmountInResponse);
    }

    @Test
    public void readJsonFileIntoAString() throws IOException {

        // Only new String works in this case to convert Byte Data into String.
        // String.valueOf works only for int. long, obj etc
        try {
            System.out.println(new String(Files.readAllBytes(Paths.get(COURSE_JSON_FILE_PATH))));
        } catch (IOException ioe) {
            ioe.getCause();
            ioe.getStackTrace();
            ioe.printStackTrace();
        }
    }

    @Test
    public void ttest() {
        System.out.println("Pretty Print : \n" + jsonPath.prettyPrint());
        System.out.println("Courses String Only : \n" + jsonPath.getString("courses"));
        System.out.println("Courses List : \n" + jsonPath.getList("courses"));
        List<Object> courses = jsonPath.getList("courses");
        List<HashMap<String, Object>> coursesHm = jsonPath.getList("courses");
        System.out.println("List<Object> Courses : \n" + courses);
        System.out.println("List<HashMap<String, Object>> Courses : \n" + coursesHm);
        System.out.println("Title of First Course : \n" + coursesHm.getFirst().get("title"));
        for (HashMap<String, Object> stringObjectHashMap : coursesHm) {
            System.out.println("Title of Course : \n" + stringObjectHashMap.get("title"));
        }
    }
}