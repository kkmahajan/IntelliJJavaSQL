package org.example.automation;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class TicketsApiTest {
    private String base;
    @BeforeEach void setup() {
        assumeTrue(Boolean.getBoolean("integration.tests"), "Enable -Dintegration.tests=true");
        base = System.getProperty("app.baseUrl", "http://localhost:8080");
        io.restassured.RestAssured.replaceFiltersWith(new ApiExecutionFilter());
    }

    private Map<String, Object> input(String title, String priority) {
        return Map.of("title", title, "description", "Customer cannot sign in", "priority", priority, "assigneeId", 1);
    }
    private Response create() {
        return given().baseUri(base).contentType(ContentType.JSON)
                .body(input("Ticket " + UUID.randomUUID(), "HIGH")).post("/api/tickets");
    }
    private void cleanup(int id) { given().baseUri(base).delete("/api/tickets/" + id).then().statusCode(204); }

    @Test void createReadAndDelete() {
        Response created = create();
        created.then().statusCode(201).header("Location", containsString("/api/tickets/"))
                .body("status", equalTo("OPEN"), "priority", equalTo("HIGH"), "assigneeId", equalTo(1));
        int id = created.jsonPath().getInt("id");
        try {
            given().baseUri(base).get("/api/tickets/" + id).then().statusCode(200).body("id", equalTo(id));
        } finally { cleanup(id); }
        given().baseUri(base).get("/api/tickets/" + id).then().statusCode(404);
    }

    @Test void progressesThroughRealWorldLifecycle() {
        int id = create().then().statusCode(201).extract().path("id");
        try {
            given().baseUri(base).contentType(ContentType.JSON).body(Map.of("status", "IN_PROGRESS"))
                    .patch("/api/tickets/" + id + "/status").then().statusCode(200).body("status", equalTo("IN_PROGRESS"));
            given().baseUri(base).contentType(ContentType.JSON).body(Map.of("status", "RESOLVED"))
                    .patch("/api/tickets/" + id + "/status").then().statusCode(200).body("status", equalTo("RESOLVED"));
        } finally { cleanup(id); }
    }

    @Test void preventsSkippingLifecycleStates() {
        int id = create().then().statusCode(201).extract().path("id");
        try {
            given().baseUri(base).contentType(ContentType.JSON).body(Map.of("status", "RESOLVED"))
                    .patch("/api/tickets/" + id + "/status").then().statusCode(409);
        } finally { cleanup(id); }
    }

    @Test void updatesDetailsWithoutChangingStatus() {
        int id = create().then().statusCode(201).extract().path("id");
        try {
            given().baseUri(base).contentType(ContentType.JSON).body(input("Updated issue", "LOW"))
                    .put("/api/tickets/" + id).then().statusCode(200)
                    .body("title", equalTo("Updated issue"), "priority", equalTo("LOW"), "status", equalTo("OPEN"));
        } finally { cleanup(id); }
    }

    @Test void filtersByPriorityAndStatus() {
        int id = create().then().statusCode(201).extract().path("id");
        try {
            given().baseUri(base).queryParam("status", "OPEN").queryParam("priority", "HIGH")
                    .get("/api/tickets").then().statusCode(200).body("id", hasItem(id));
            given().baseUri(base).queryParam("status", "RESOLVED")
                    .get("/api/tickets").then().statusCode(200).body("id", not(hasItem(id)));
        } finally { cleanup(id); }
    }

    @Test void rejectsMissingAssignee() {
        given().baseUri(base).contentType(ContentType.JSON)
                .body(Map.of("title", "Broken login", "priority", "HIGH", "assigneeId", 999999))
                .post("/api/tickets").then().statusCode(400);
    }

    @Test void rejectsInvalidPriorityAndTitle() {
        given().baseUri(base).contentType(ContentType.JSON)
                .body(Map.of("title", "Help", "priority", "CRITICAL"))
                .post("/api/tickets").then().statusCode(400);
        given().baseUri(base).contentType(ContentType.JSON)
                .body(Map.of("title", " ", "priority", "LOW"))
                .post("/api/tickets").then().statusCode(400);
    }

    @Test void rejectsInvalidFilters() {
        given().baseUri(base).queryParam("status", "CLOSED")
                .get("/api/tickets").then().statusCode(400);
        given().baseUri(base).queryParam("priority", "URGENT")
                .get("/api/tickets").then().statusCode(400);
    }

    @Test void returns404ForMissingTicket() {
        given().baseUri(base).get("/api/tickets/999999").then().statusCode(404);
        given().baseUri(base).delete("/api/tickets/999999").then().statusCode(404);
        given().baseUri(base).contentType(ContentType.JSON).body(Map.of("status", "IN_PROGRESS"))
                .patch("/api/tickets/999999/status").then().statusCode(404);
    }
}
