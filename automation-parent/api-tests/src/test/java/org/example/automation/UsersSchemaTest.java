package org.example.automation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class UsersSchemaTest {
    private String baseUrl;

    @BeforeEach void configure() {
        assumeTrue(Boolean.getBoolean("integration.tests"), "Start the sample app and use -Dintegration.tests=true");
        baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
    }

    @Test void individualUserMatchesContract() {
        given().baseUri(baseUrl).get("/api/users/1").then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user.json"));
    }

    @Test void userListContainsSchemaValidUsers() {
        given().baseUri(baseUrl).get("/api/users").then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/users.json"));
    }
}
