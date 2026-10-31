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

## Phase 4 — Selenium Page Objects and Playwright Java

The ui-tests module now includes:
- UiTestSupport: reusable ChromeDriver lifecycle, optional headless mode and configurable app URL.
- UserManagerPage: Selenium Page Object with explicit waits for user CRUD operations.
- UserManagerCrudUiTest: create/search/edit/delete and duplicate-email UI checks.
- PlaywrightUserManagerTest: independent Playwright Chromium CRUD scenario.
- Existing UserManagerUiTest smoke test remains available.

The sample app must be running at http://localhost:8080 (or set -Dapp.baseUrl=...).

```powershell
# Offline reactor tests (UI suites skip unless opted in):
mvn clean test

# Selenium smoke + CRUD (Chrome installed):
mvn -pl ui-tests clean test "-Dui.tests=true"

# Install Playwright Chromium once (PowerShell, from automation-parent):
mvn -pl ui-tests dependency:build-classpath "-Dmdep.outputFile=ui-tests/target/playwright-classpath.txt"
$classpath = Get-Content ui-tests/target/playwright-classpath.txt
java -cp $classpath com.microsoft.playwright.CLI install chromium

# Playwright CRUD (separate opt-in flag):
mvn -pl ui-tests clean test "-Dplaywright.tests=true"

# For visible browser windows add "-Dui.headless=false"
```

Selenium and Playwright mutate the in-memory user store with uniquely named test users and attempt cleanup. Run browser suites against the local disposable sample app, not production.

## Phase 5 — GitHub Actions CI and diagnostics

Workflow: `.github/workflows/automation-framework-ci.yml` (relative to repository root).

It triggers only for pushes to `feature/multi-module-test-framework` that change `automation-parent/**` or the workflow itself, plus manual `workflow_dispatch`. It does not run on or update `master`.

The Ubuntu/Java 21 job:
1. Builds the Maven reactor and runs offline Mockito/WireMock tests.
2. Starts the sample Spring Boot app and polls `/api/users/1` for readiness.
3. Runs REST Assured CRUD and JSON Schema tests against the app.
4. Installs Chrome and runs Selenium smoke/CRUD tests.
5. Installs Playwright Chromium and runs the Playwright CRUD test.
6. Uploads Surefire XML/text reports, app logs and UI failure screenshots, even if a step fails.

Inspect results in GitHub → Actions → Automation Framework CI → selected run. Download the `automation-framework-reports-...` artifact for diagnostic files (retained for 14 days).

The Selenium `UiFailureCapture` JUnit extension takes screenshots before WebDriver teardown when a test fails. Playwright captures a screenshot when its CRUD scenario throws. No screenshots are produced for passing tests. Browser test suites modify only the disposable local in-memory application.
