import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.SerializationFeature;
import reporter.ExtentManager;
import reporter.ExtentTestManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RestAssuredTestOne {

    // Given - All input details
    // When - Submit the API
    // Then - Validate the response

    @Test
    public void TestOne(String[] args) {
        RestAssured.baseURI = "https://rahulshettyacademy.com/maps/api/place/";
        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body(createRequestBodyForAddPlace()).
        when().post("add/json").
        then().log().all().assertThat().statusCode(200).body("scope", equalTo("APP"));

        ExtentTestManager.getExtentTest().log(Status.INFO, "Additional information...");

        ExtentTestManager.getExtentTest().pass("Test passed!");
        ExtentManager.flush(); // Flush ExtentReports
    }

    public static Object createRequestBodyForAddPlace(){
        HashMap<String, Object> requestBodyAddPlace = new HashMap<>();
        HashMap<String, Object> location = new HashMap<>();
        ArrayList<String> typesData = new ArrayList<>();

        typesData.add("shoePark");
        typesData.add("shoe");

        Random random = new Random();
        double latitude = random.nextDouble() * 180 - 90; // Generate a random value between -90 and 90
        double longitude = random.nextDouble() * 360 - 180; // Generate a random value between -180 and 180

        location.put("lat", latitude);
        location.put("lng", longitude);

        requestBodyAddPlace.put("location", location);
        requestBodyAddPlace.put("types", typesData);
        requestBodyAddPlace.put("accuracy", 50);
        requestBodyAddPlace.put("name", "Frontline House");
        requestBodyAddPlace.put("phone_number", "(+91)0987654321");
        requestBodyAddPlace.put("address", random.nextInt(100) +", side layout, cohen 09");
        requestBodyAddPlace.put("website", "http://google.com");
        requestBodyAddPlace.put("language", "English-EN");

        return requestBodyAddPlace;
    }

//    @Test
//    public void printJson() throws JsonProcessingException {
//
//        Object obj = createRequestBodyForAddPlace();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//        System.out.println("Following is the JSON Format of the HashMap :\n"+objectMapper.writeValueAsString(obj));
//    }
}
