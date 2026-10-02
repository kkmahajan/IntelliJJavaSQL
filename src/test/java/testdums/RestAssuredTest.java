package testdums;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestAssuredTest {

    private static final String BASE_URI = "https://rahulshettyacademy.com/maps/api/place/";
    private static final String API_KEY_ENV = "MAPS_API_KEY";

    @Test
    public void shouldAddUpdateAndReadPlace() {
        String apiKey = requireApiKey();
        RestAssured.baseURI = BASE_URI;

        String addResponse = given()
                .contentType("application/json")
                .queryParam("key", apiKey)
                .body(createAddPlaceRequest(-1.086320854785452, -124.684349682051))
                .when()
                .post("add/json")
                .then()
                .statusCode(200)
                .body("scope", equalTo("APP"))
                .extract()
                .asString();

        String placeId = JsonPath.from(addResponse).getString("place_id");
        Assert.assertNotNull(placeId);
        Assert.assertFalse(placeId.isBlank());

        String updatedAddress = "BackLine House";
        given()
                .contentType("application/json")
                .queryParam("key", apiKey)
                .body(createUpdatePlaceRequest(placeId, updatedAddress, apiKey))
                .when()
                .put("update/json")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Address successfully updated"));

        String actualAddress = given()
                .queryParam("key", apiKey)
                .queryParam("place_id", placeId)
                .when()
                .get("get/json")
                .then()
                .statusCode(200)
                .extract()
                .path("address");

        Assert.assertEquals(actualAddress, updatedAddress);
    }

    @Test(dataProvider = "coordinates")
    public void shouldBuildRequestForCoordinates(double latitude, double longitude) {
        Map<String, Object> request = createAddPlaceRequest(latitude, longitude);
        @SuppressWarnings("unchecked")
        Map<String, Double> location = (Map<String, Double>) request.get("location");

        Assert.assertEquals(location.get("lat"), latitude);
        Assert.assertEquals(location.get("lng"), longitude);
        Assert.assertEquals(request.get("types"), List.of("shoePark", "shoe"));
    }

    @Test
    public void shouldFilterAndTransformStreamsPredictably() {
        List<Integer> values = List.of(0, 10, 5, 20, 25, 15);

        Assert.assertEquals(values.stream().filter(value -> value % 2 == 0).toList(), List.of(0, 10, 20));
        Assert.assertEquals(values.stream().filter(value -> value % 5 == 0).toList(), values);
        Assert.assertEquals(values.stream().map(value -> value * 2).toList(), List.of(0, 20, 10, 40, 50, 30));
    }

    @DataProvider(name = "coordinates")
    public Object[][] coordinates() {
        return new Object[][]{
                {-1.086320854785452, -124.684349682051},
                {-6.086320854745452, -124.984149684051},
                {-1.086322354745452, -124.6834684051}
        };
    }

    private Map<String, Object> createAddPlaceRequest(double latitude, double longitude) {
        return Map.of(
                "location", Map.of("lat", latitude, "lng", longitude),
                "accuracy", 50,
                "name", "Frontline House",
                "phone_number", "(+91)0987654321",
                "address", "1, side layout, cohen 09",
                "types", List.of("shoePark", "shoe"),
                "website", "https://example.com",
                "language", "English-EN"
        );
    }

    private Map<String, Object> createUpdatePlaceRequest(String placeId, String address, String apiKey) {
        return Map.of(
                "place_id", placeId,
                "address", address,
                "key", apiKey
        );
    }

    private String requireApiKey() {
        String apiKey = System.getenv(API_KEY_ENV);
        if (apiKey == null || apiKey.isBlank()) {
            throw new SkipException("Integration test skipped: set " + API_KEY_ENV + " to run it.");
        }
        return apiKey;
    }
}
