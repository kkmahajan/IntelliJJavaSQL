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
