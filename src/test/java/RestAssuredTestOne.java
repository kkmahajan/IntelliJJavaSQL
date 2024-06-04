import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Stream;

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
        String addResponse = given()
                .log()
                .all()
                .queryParam("key", key)
                .header("Content-Type", "application/json")
                .body(createRequestBodyForAddPlace())
                .when()
                .post("add/json")
                .then().assertThat()
                .statusCode(200)
                .body("scope", equalTo("APP"))
                .extract()
                .response()
                .asString();

        JsonPath jsonPath = new JsonPath(addResponse);
        place_id = jsonPath.getString("place_id");
        System.out.println("Place Id : " + place_id);

        // Update Place
        given().log().all().queryParam("key", "qaclick123").header("Content-Type", "application/json").body(createRequestBodyForUpdatePlace()).when().put("update/json").then().assertThat().log().all().statusCode(200).body("msg", equalTo("Address successfully updated"));


        // Get Place
        String getPlaceResponse = given()
                .log()
                .all()
                .queryParam("key", key)
                .queryParam("place_id", place_id)
                .when()
                .get("get/json")
                .then()
                .assertThat()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        JsonPath jsonPath1 = new JsonPath(getPlaceResponse);
        String actualAddress = jsonPath1.getString("address");
        System.out.println("The Actual Address in Get Place after Update Place Response is : " + actualAddress);
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

    @Test
    public void testStringArray() {
        String[] stringArray = {"Kaustubh", "Mahajan", "Shruti", "Mahajan"};

        //Prints only the object name reference
        System.out.println(stringArray);

        //Prints the actual content of the String Array
        System.out.println(Arrays.toString(stringArray));

        String[][] string2darray = {stringArray, stringArray};
        System.out.println(Arrays.deepToString(string2darray));

        System.out.println("Below is a Stream of String");
        Stream<String> stringStream = Arrays.stream(stringArray);
        stringStream.forEach(s -> System.out.println(s + " " + 1234));
    }

    @Test
    public void testVarInJava() {
        var a = "Goggle";
        var b = 232;
        var c = true;
        var d = 4.3;
        var e = 'A';
        var f = new String[]{"K", "K", "M"};
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
        System.out.println(Arrays.toString(f));
    }

    @Test
    public void testCode() {
        int a = 10;
        Integer b = a;
        System.out.println(b);

        Integer c = 20;
        int d = c;
        System.out.println(d);
    }

    @Test
    public void testFinal() {
        final int a = 120;
        final int b;
        b = 123;
        //a=321; b = 121; will get error on these lines as "a" cannot be assigned any new value
        System.out.println(a + " " + b);
        System.out.println(a + b);
    }

    @Test
    public void testMath() {
        System.out.println(Math.random());
        System.out.println((int) (Math.random() * 3) + 1);
    }

    @Test
    public void testStreams() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(10);
        list.add(5);
        list.add(20);
        list.add(25);
        list.add(15);
        System.out.println("Before filtering : " + list);
        List<Integer> newList = list.stream().filter(I -> I % 2 == 0).toList();
        System.out.println("After Filtering : " + newList);

        List<Integer> divBy5 = list.stream().filter(I -> I % 5 == 0).toList();
        System.out.println("Filtering by divisible by 5 : " + divBy5);

        List<Integer> temp = list.stream().filter(I -> I.toString().contains("0")).toList();
        System.out.println("Filtering by Zero in number :" + temp);

        List<Integer> multiplyBy2 = list.stream().map(I -> I * 2).toList();
        System.out.println("Multiple each element in list by 2 : " + multiplyBy2);
        System.out.println("Multiple each element in list by 2 & count : " + (long) multiplyBy2.size());
        System.out.println("Multiple each element in list by 2 & count : " + (long) multiplyBy2.size());
        System.out.println("Multiple each element in list by 2 & sorted : " + multiplyBy2.stream().sorted().toList());
    }

    @Test
    public void testHashSet() {
        HashSet<Integer> re = new HashSet<>();
        re.add(1);
        re.add(1);
        re.add(12);
//        re.add(null);  Does not work with Iterator, but works normally
        re.add(34);
        re.add(0);
        re.add(46);
        re.add(48);
        re.add(31);
        System.out.println(re);
        Iterator<Integer> i = re.iterator();
        while (i.hasNext()) {
            Integer I = i.next();
            if (I == 0) {
                I = i.next();
            }
            if (I % 2 == 0) {
                System.out.println(I + " is an even number");
            } else {
                i.remove();
            }
        }
        System.out.println(re);
    }

    @Test
    public void testHashMap() {
        Map<Object, String> mymap = new HashMap<>();
        mymap.put(null, "null");
        mymap.put(3, "3");
        mymap.put(5, "5");
        mymap.put(0, "0");
        mymap.put("Zero", "0");
        System.out.println(mymap.get(null));
        mymap.remove(null);
        System.out.println(mymap);
    }

    @Test
    public void testEnvVariables() {
        System.out.println(System.getenv());
    }
}
