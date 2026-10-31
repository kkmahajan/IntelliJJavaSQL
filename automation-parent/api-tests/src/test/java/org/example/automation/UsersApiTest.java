package org.example.automation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static io.restassured.RestAssured.given;

class UsersApiTest {
    @Test
    void shouldReturnSampleUser() {
        String baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
        assumeTrue(Boolean.getBoolean("integration.tests"), "Run with -Dintegration.tests=true after starting the sample app");
        String name = given().baseUri(baseUrl).when().get("/api/users/1")
                .then().statusCode(200).extract().path("name");
        assertEquals("John Watson", name);
    }
}
