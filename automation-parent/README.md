# Multi-module test automation (phase 1)

This is an isolated Maven reactor alongside the original project. The original root `pom.xml` and tests remain unchanged.

Modules:
- `sample-app`: Spring Boot REST API and locally hosted HTML UI.
- `api-tests`: JUnit 5 + REST Assured sample API integration test.
- `ui-tests`: JUnit 5 + Selenium sample UI smoke test.

Requirements: Java 21, Maven, and Chrome/compatible Selenium driver setup for UI execution.

From `automation-parent`:

```bash
mvn clean test
mvn -pl sample-app spring-boot:run
```

In another terminal, with the app running:

```bash
mvn -pl api-tests test -Dintegration.tests=true
mvn -pl ui-tests test -Dui.tests=true
```

Use `-Dapp.baseUrl=http://localhost:8080` to override the target.

The API/UI integration tests skip by default so ordinary offline builds don't require a running server or browser.

Next phases: Mockito, WireMock, JSON Schema, Playwright, Cucumber, Testcontainers, JMeter, accessibility, reporting and CI. The code has not yet been built in this environment.
