# Multi-module test automation — Phase 2

This isolated Maven reactor does not change the repository root pom.xml.

## Modules
- sample-app: Spring Boot User Manager API and browser UI; in-memory data resets on restart.
- api-tests: JUnit 5 and REST Assured CRUD and negative integration tests.
- ui-tests: JUnit 5 and Selenium UI smoke test.

Requires Java 21+, Maven, and Chrome for UI tests.

From automation-parent, build and start the application:

```bash
mvn clean test
mvn -pl sample-app spring-boot:run
```

In a second terminal, while the application is running:

```bash
mvn -pl api-tests test -Dintegration.tests=true
mvn -pl ui-tests test -Dui.tests=true
```

Use -Dapp.baseUrl=http://localhost:8080 to override the target. Integration tests skip by default.

## REST API
- GET /api/users: list users
- GET /api/users/{id}: fetch or 404
- POST /api/users: create with JSON {name,email}; returns 201 and Location
- PUT /api/users/{id}: update or 404
- DELETE /api/users/{id}: delete (204) or 404
- Invalid name/email: 400; duplicate email: 409

The sample user (id 1, John Watson) exists on startup. The local UI supports search, create, edit, and delete. All data is in memory and resets when the application restarts. The API integration tests create unique records and clean up afterward.

This is a local automation demonstration, not a production system with authentication or guaranteed transactional concurrency. Future phases: Mockito, WireMock, JSON Schema, Playwright, Cucumber, Testcontainers, performance, accessibility, reporting, CI.

## Phase 3 — JUnit 5, Mockito, WireMock and JSON Schema

The controller delegates to UserService, which uses a UserRepository interface backed by InMemoryUserRepository. Mockito tests in sample-app verify validation, create/update/delete and not-found behavior without starting the web server.

WireMockExternalApiTest in api-tests starts an ephemeral local WireMock server and tests a Java HttpClient against success, HTTP 500, invalid payload and delayed-response timeout. These tests do not need the sample app or internet access.

UsersSchemaTest in api-tests checks the GET /api/users/1 response against src/test/resources/schemas/user.json and verifies the list response. These integration tests require the running sample app.

Run from automation-parent:

```bash
mvn -pl sample-app clean test
mvn -pl api-tests clean test
mvn -pl sample-app spring-boot:run
```

In another terminal while sample-app runs:

```bash
mvn -pl api-tests clean test -Dintegration.tests=true
mvn -pl ui-tests clean test -Dui.tests=true
```

Note: The first two commands run the offline Mockito and WireMock tests; the API integration tests skip unless integration.tests=true. Run the entire reactor with mvn clean test to include all offline tests.
