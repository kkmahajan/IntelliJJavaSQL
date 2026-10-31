package org.example.automation;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.hamcrest.Matchers.*;

class UsersApiTest {
    private String baseUrl;

    @BeforeEach
    void configure() {
        assumeTrue(Boolean.getBoolean("integration.tests"), "Start the sample app and use -Dintegration.tests=true");
        baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
    }

    @Test
    void listsAndGetsSeedUser() {
        given().baseUri(baseUrl).get("/api/users").then().statusCode(200)
                .body("id", hasItem(1), "name", hasItem("John Watson"));
        given().baseUri(baseUrl).get("/api/users/1").then().statusCode(200)
                .body("name", equalTo("John Watson"), "email", equalTo("john@example.com"));
    }

    @Test
    void missingUserReturns404() {
        given().baseUri(baseUrl).get("/api/users/999999").then().statusCode(404);
        given().baseUri(baseUrl).delete("/api/users/999999").then().statusCode(404);
        given().baseUri(baseUrl).contentType(ContentType.JSON)
                .body(Map.of("name", "Nobody", "email", "nobody@example.com"))
                .put("/api/users/999999").then().statusCode(404);
    }

    @Test
    void createUpdateAndDeleteUser() {
        String uniqueEmail = "test-" + java.util.UUID.randomUUID() + "@example.com";
        Response created = given().baseUri(baseUrl).contentType(ContentType.JSON)
                .body(Map.of("name", "API Test User", "email", uniqueEmail))
                .post("/api/users");
        created.then().statusCode(201).header("Location", containsString("/api/users/"));
        int id = created.jsonPath().getInt("id");
        try {
            given().baseUri(baseUrl).get("/api/users/" + id).then().statusCode(200)
                    .body("email", equalTo(uniqueEmail));
            given().baseUri(baseUrl).contentType(ContentType.JSON)
                    .body(Map.of("name", "Updated Test User", "email", uniqueEmail))
                    .put("/api/users/" + id).then().statusCode(200)
                    .body("name", equalTo("Updated Test User"));
        } finally {
            given().baseUri(baseUrl).delete("/api/users/" + id).then().statusCode(204);
        }
        given().baseUri(baseUrl).get("/api/users/" + id).then().statusCode(404);
    }

    @Test
    void rejectsInvalidInputAndDuplicateEmail() {
        given().baseUri(baseUrl).contentType(ContentType.JSON)
                .body(Map.of("name", "", "email", "not-an-email"))
                .post("/api/users").then().statusCode(400);
        given().baseUri(baseUrl).contentType(ContentType.JSON)
                .body(Map.of("name", "Duplicate", "email", "JOHN@example.com"))
                .post("/api/users").then().statusCode(409);
    }
}
