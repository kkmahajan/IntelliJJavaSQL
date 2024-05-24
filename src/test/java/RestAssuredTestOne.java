import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestAssuredTestOne {

    String key = "qaclick123";
    String place_id = null;
    Random random = new Random();

    // Given - All input details
    // When - Submit the API
    // Then - Validate the response

    @Test
    public void TestOne() {
        RestAssured.baseURI = "https://rahulshettyacademy.com/maps/api/place/";
        String addResponse = given().log().all().queryParam("key", key).header("Content-Type", "application/json").body(createRequestBodyForAddPlace()).when().post("add/json").then().assertThat().statusCode(200).body("scope", equalTo("APP")).extract().response().asString();

        JsonPath jsonPath = new JsonPath(addResponse);
        place_id = jsonPath.getString("place_id");
        System.out.println("Place Id : " + place_id);

        // Update Place
        given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json").body(createRequestBodyForUpdatePlace()).when().put("update/json").then().assertThat().log().all().statusCode(200).body("msg", equalTo("Address successfully updated"));


        // Get Place
        String getPlaceResponse = given().log().all().queryParam("key", key).queryParam("place_id", place_id).when().get("get/json").then().assertThat().log().all().statusCode(200).extract().response().asString();

        JsonPath jsonPath1 = new JsonPath(getPlaceResponse);
        String actualAddress = jsonPath1.getString("address");
        System.out.println("The Actual Address in Get Place after Update Place Response is : " + actualAddress);

//        ExtentTestManager.getExtentTest().log(Status.INFO, "Additional information...");
//        ExtentTestManager.getExtentTest().pass("Test passed!");
//        ExtentManager.flush(); // Flush ExtentReports
    }

    public Object createRequestBodyForAddPlace() {
        HashMap<String, Object> requestBodyAddPlace = new HashMap<>();
        HashMap<String, Object> location = new HashMap<>();
        ArrayList<String> typesData = new ArrayList<>();

        typesData.add("shoePark");
        typesData.add("shoe");

        double latitude = random.nextDouble() * 180 - 90; // Generate a random value between -90 and 90
        double longitude = random.nextDouble() * 360 - 180; // Generate a random value between -180 and 180
        location.put("lat", latitude);
        location.put("lng", longitude);
        String placeNumber = String.valueOf(1 + random.nextInt(99));

        requestBodyAddPlace.put("location", location);
        requestBodyAddPlace.put("types", typesData);
        requestBodyAddPlace.put("accuracy", 50);
        requestBodyAddPlace.put("name", placeNumber + ", Frontline House");
        requestBodyAddPlace.put("phone_number", "(+91)0987654321");
        requestBodyAddPlace.put("address", random.nextInt(100) + ", side layout, cohen 09");
        requestBodyAddPlace.put("website", "http://google.com");
        requestBodyAddPlace.put("language", "English-EN");

        return requestBodyAddPlace;
    }

    @Test
    public Object createRequestBodyForUpdatePlace() {
        HashMap<String, Object> requestBodyUpdatePlace = new HashMap<>();

        requestBodyUpdatePlace.put("place_id", place_id);
        requestBodyUpdatePlace.put("address", "BackLine House");
        requestBodyUpdatePlace.put("key", key);

        return requestBodyUpdatePlace;
    }

    @Test
    public void printPrettyJson() throws JsonProcessingException {

        Object obj = createRequestBodyForAddPlace();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println("Following is the JSON Format of the HashMap :\n" + objectMapper.writeValueAsString(obj));
    }

    @DataProvider(name = "LatLongData")
    public Object[][] latLongData() {
        return new Object[][]{{-1.086320854785452, -124.684349682051}, {-6.086320854745452, -124.984149684051}, {-1.086322354745452, -124.6834684051}};
    }

    @Test(dataProvider = "LatLongData")
    public void addPlaceWithDataProvider(double latitude, double longitude) {

        HashMap<String, Object> requestBodyAddPlace = new HashMap<>();
        HashMap<String, Object> location = new HashMap<>();
        ArrayList<String> typesData = new ArrayList<>();

        typesData.add("shoePark");
        typesData.add("shoe");

        location.put("lat", latitude);
        location.put("lng", longitude);
        String placeNumber = String.valueOf(1 + random.nextInt(99));

        requestBodyAddPlace.put("location", location);
        requestBodyAddPlace.put("types", typesData);
        requestBodyAddPlace.put("accuracy", 50);
        requestBodyAddPlace.put("name", placeNumber + ", Frontline House");
        requestBodyAddPlace.put("phone_number", "(+91)0987654321");
        requestBodyAddPlace.put("address", random.nextInt(100) + ", side layout, cohen 09");
        requestBodyAddPlace.put("website", "http://google.com");
        requestBodyAddPlace.put("language", "English-EN");


        RestAssured.baseURI = "https://rahulshettyacademy.com/maps/api/place/";
        String addResponseDataProvider = given().log().all().queryParam("key", key).header("Content-Type", "application/json").body(requestBodyAddPlace).when().post("add/json").then().assertThat().statusCode(200).body("scope", equalTo("APP")).extract().response().asString();

        JsonPath jsonPath = new JsonPath(addResponseDataProvider);
        System.out.println(jsonPath);
    }
}
