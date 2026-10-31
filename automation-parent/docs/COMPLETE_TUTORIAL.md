# Complete tutorial: Java multi-module test automation framework

> **Scope:** This is a hands-on explanation of the actual code in `automation-parent/`, through Phases 1–8 (including the Service Desk application, dual reporting and GitHub Pages deployment). It is not a generic framework template. Follow the paths and commands from this repository. Verified reporting CI: [successful GitHub Actions run 41225043401](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41225043401) (both reports generated and published). GitHub Pages deployment was added afterward and requires a separate successful deployment to verify the live URLs.
>
> **Branch safety:** Development is on `feature/multi-module-test-framework`. The CI workflow is limited to this branch; do not assume it runs on `master`.

## Contents

17. [Phase 8: Service Desk business workflow and scalable testing](#17-phase-8-service-desk-business-workflow-and-scalable-testing)

1. [What we built and why](#1-what-we-built-and-why)
2. [Prerequisites and first run](#2-prerequisites-and-first-run)
3. [Maven reactor and module structure](#3-maven-reactor-and-module-structure)
4. [Spring Boot sample application and REST design](#4-spring-boot-sample-application-and-rest-design)
5. [JUnit 5 and Mockito unit tests](#5-junit-5-and-mockito-unit-tests)
6. [REST Assured API integration tests](#6-rest-assured-api-integration-tests)
7. [JSON Schema contract testing](#7-json-schema-contract-testing)
8. [WireMock and external dependency simulation](#8-wiremock-and-external-dependency-simulation)
9. [Selenium smoke, Page Objects and CRUD](#9-selenium-smoke-page-objects-and-crud)
10. [Playwright Java](#10-playwright-java)
11. [GitHub Actions CI pipeline](#11-github-actions-ci-pipeline)
12. [Reports, screenshots and troubleshooting](#12-reports-screenshots-and-troubleshooting)
13. [How to extend the project](#13-how-to-extend-the-project)
14. [Test inventory and limitations](#14-test-inventory-and-limitations)

## 1. What we built and why

This repository has a **real local system under test**, not just a collection of tests that call third-party websites. The Spring Boot User Manager serves a REST API and a browser UI; tests exercise its business logic, HTTP contracts, and user journeys.

| Layer | Technology | What it proves |
| --- | --- | --- |
| Application | Spring Boot 3.4.2, Java records, in-memory repository | There is a repeatable local application to test |
| Unit | JUnit Jupiter 5.11.4, Mockito 5.15.2 | Business rules can be tested without a running server |
| API integration | REST Assured 5.5.0 | Real HTTP endpoints return correct status, headers and data |
| Contract | REST Assured JSON Schema validator | Responses retain expected JSON structure |
| External API simulation | WireMock 3.9.2, Java HttpClient | Error/timeout scenarios are reproducible offline |
| Browser | Selenium 4.28.1, ChromeDriver | UI works through a browser, with reusable Page Objects |
| Browser alternative | Playwright Java 1.56.0 | Same CRUD journey can be tested with Playwright locators |
| CI | GitHub Actions, Temurin Java 21, Ubuntu 24.04 | Tests run automatically on a clean hosted runner |

The work was built incrementally: **Phase 1** introduced the multi-module application and smoke tests; **Phase 2** added user CRUD; **Phase 3** introduced service/repository separation, Mockito, WireMock and schemas; **Phase 4** added Selenium Page Objects and Playwright; **Phase 5** automated execution, reports and failure screenshots.

## 2. Prerequisites and first run

Install Java **21 or newer**, Maven 3.x, Git and Google Chrome. Java 21 is the compiler target and CI version; using a newer local JDK is possible but may produce additional warnings. Playwright downloads its own Chromium build. The commands below run from the **`automation-parent` directory**, not the repository root.

### 2.1 Clone and select the working branch

```powershell
git clone https://github.com/kkmahajan/IntelliJJavaSQL.git
cd IntelliJJavaSQL
git switch feature/multi-module-test-framework
cd automation-parent
java -version
mvn -version
```

If you already have the repository, use `git switch feature/multi-module-test-framework`, `git pull --ff-only` and then `cd automation-parent`. On Windows, if `mvn` is not in PATH, use the bundled Maven executable from IntelliJ IDEA, e.g.:

```powershell
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.1\plugins\maven\lib\maven3\bin\mvn.cmd"
& $mvn clean test
```

In the following commands `mvn` means your available Maven executable; substitute `& $mvn` on PowerShell if necessary.

### 2.2 Run tests that do not need the app

```powershell
mvn clean test
```

This runs the reactor. JUnit assumptions intentionally skip the HTTP and browser tests unless you enable their system properties. Expect unit and WireMock tests to run, **not all 26 tests in this one command**.

### 2.3 Start the app (terminal 1)

```powershell
mvn -pl sample-app spring-boot:run
```

Visit `http://localhost:8080` for the UI and `http://localhost:8080/api/users/1` for a sample JSON response. Leave the process running while testing. To stop, press Ctrl+C.

### 2.4 Run integration and browser tests (terminal 2)

```powershell
mvn -pl api-tests clean test "-Dintegration.tests=true"
mvn -pl ui-tests clean test "-Dui.tests=true"
```

Chrome is needed for Selenium. For Playwright, install Chromium once, **from `automation-parent`**:

```powershell
mvn -pl ui-tests dependency:build-classpath "-Dmdep.outputFile=target/playwright-classpath.txt"
$classpath = Get-Content "ui-tests\target\playwright-classpath.txt"
java -cp $classpath com.microsoft.playwright.CLI install chromium
mvn -pl ui-tests clean test "-Dplaywright.tests=true"
```

To watch browsers interact, add `"-Dui.headless=false"` to the Selenium or Playwright command. To target another local app instance, add `"-Dapp.baseUrl=http://localhost:8081"`; the application must already be running at that URL.

## 3. Maven reactor and module structure

```text
IntelliJJavaSQL/
├── .github/workflows/automation-framework-ci.yml
└── automation-parent/
    ├── pom.xml                         # parent Maven reactor
    ├── README.md                       # quick start and project index
    ├── docs/COMPLETE_TUTORIAL.md       # this tutorial
    ├── sample-app/
    │   ├── pom.xml
    │   └── src/
    │       ├── main/java/org/example/automation/
    │       │   ├── UserController.java
    │       │   ├── UserService.java
    │       │   ├── UserRepository.java
    │       │   └── InMemoryUserRepository.java
    │       ├── main/resources/static/index.html
    │       └── test/java/org/example/automation/UserServiceTest.java
    ├── api-tests/
    │   ├── pom.xml
    │   └── src/test/
    │       ├── java/org/example/automation/
    │       │   ├── UsersApiTest.java
    │       │   ├── UsersSchemaTest.java
    │       │   ├── ExternalApiClient.java
    │       │   └── WireMockExternalApiTest.java
    │       └── resources/schemas/
    │           ├── user.json
    │           └── users.json
    └── ui-tests/
        ├── pom.xml
        └── src/test/java/org/example/automation/
            ├── UserManagerUiTest.java
            ├── UiTestSupport.java
            ├── UiFailureCapture.java
            ├── UserManagerPage.java
            ├── UserManagerCrudUiTest.java
            └── PlaywrightUserManagerTest.java
```

The parent `pom.xml` has `<packaging>pom</packaging>` and declares the three modules. It centrally specifies `maven.compiler.release=21`, JUnit BOM 5.11.4, compiler plugin 3.13.0 and Surefire 3.5.2. Each child declares only its relevant dependencies.

Why isolate `automation-parent`? The original repository root project remains independent. Maven `-pl api-tests` selects only the API module; `-pl sample-app` selects the application; `-am` also makes required reactor modules. `mvn clean test` at the parent walks the full reactor.

**Key Maven concepts:** `test` compiles and runs JUnit tests; `clean` deletes old `target/` output; `-Dkey=value` passes Java system properties to Surefire; `-B` runs Maven in batch mode; `-ntp` suppresses transfer progress.

## 4. Spring Boot sample application and REST design

The architecture follows a simple request flow:

```text
Browser UI / REST Assured / curl
           |
           v
UserController (@RestController, /api/users)
           |
           v
UserService (@Service: validation and CRUD rules)
           |
           v
UserRepository (interface)
           |
           v
InMemoryUserRepository (@Repository, ConcurrentHashMap)
```

`UserController.java` declares two Java records: `User(int id, String name, String email)` is the response model; `UserInput(String name, String email)` is the incoming request body. The controller delegates to `UserService` and maps HTTP routes with `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`.

| Request | Expected success | Important negative behavior |
| --- | --- | --- |
| `GET /api/users` | `200`, sorted user array | — |
| `GET /api/users/1` | `200`, user object | Unknown ID: `404` |
| `POST /api/users` | `201`, created user and `Location` header | Invalid input: `400`; duplicate email: `409` |
| `PUT /api/users/{id}` | `200`, updated user | Unknown ID: `404`; invalid input: `400` |
| `DELETE /api/users/{id}` | `204`, empty body | Unknown ID: `404` |

Try the API from PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/users
$body = @{ name = "Tutorial User"; email = "tutorial@example.com" } | ConvertTo-Json
$created = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/users -ContentType "application/json" -Body $body
$created
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/users/$($created.id)"
```

`UserService` validates nonblank names and basic email syntax, trims fields, rejects duplicate emails **case-insensitively**, and throws `ResponseStatusException` for `400`, `404`, or `409`. It generates IDs with `AtomicInteger`. `InMemoryUserRepository` uses a `ConcurrentHashMap`, sorts list results by ID, and seeds **John Watson** (`id=1`, `john@example.com`) at startup.

**Important limitations:** Data disappears on application restart; this is not a persistent database, authentication system, or fully transactional multi-user application. The email regex is deliberately basic, not a complete email-standard implementation.

The browser UI is `sample-app/src/main/resources/static/index.html`. It uses `fetch` to call the same API, and stable element IDs such as `#name`, `#email`, `#save`, `#search`, `#users`, and `#message`. The tests use those IDs to interact with the page.

## 5. JUnit 5 and Mockito unit tests

Read `sample-app/src/test/java/org/example/automation/UserServiceTest.java`. It mocks the **repository**, not the service under test:

```java
repository = mock(UserRepository.class);
when(repository.findAll()).thenReturn(List.of(seed));
service = new UserService(repository);
```

This avoids Spring startup and real HTTP calls. A test can control repository behavior precisely:

```java
when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
UserController.User user = service.create(
    new UserController.UserInput(" Alice ", " alice@example.com ")
);
assertEquals("Alice", user.name());
verify(repository).save(user);
```

The test suite checks listing, lookup, trimming, updating, deleting, missing users, blank names, null input, duplicate email, and a parameterized list of invalid emails. `@BeforeEach` prepares fresh mocks for each test; `@Test` identifies ordinary cases; `@ParameterizedTest` plus `@ValueSource` runs one logical test against multiple inputs. `assertThrows` verifies failures; `verify(..., never())` checks that invalid input is **not persisted**.

Run only this layer:

```powershell
mvn -pl sample-app clean test
```

**When to add a unit test:** Any new rule in `UserService` should get a fast, isolated unit test, including its failure path, before adding a slower browser test.

## 6. REST Assured API integration tests

Read `api-tests/src/test/java/org/example/automation/UsersApiTest.java`. Unlike Mockito tests, these use the **running Spring Boot server**.

The `@BeforeEach` method contains:

```java
assumeTrue(Boolean.getBoolean("integration.tests"),
    "Start the sample app and use -Dintegration.tests=true");
baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
```

JUnit **assumptions** skip tests when the flag is absent; skipping is not passing. REST Assured follows the readable **given / when / then** style:

```java
given().baseUri(baseUrl)
    .get("/api/users/1")
    .then().statusCode(200)
    .body("name", equalTo("John Watson"));
```

The four test methods verify: seed user and listing; missing-user `404` on GET/PUT/DELETE; create-update-delete lifecycle; invalid input `400` and case-insensitive duplicate email `409`. For created records, the suite uses a random UUID email and a `try/finally` cleanup. That reduces test collisions and keeps the in-memory state predictable.

```powershell
mvn -pl api-tests clean test "-Dintegration.tests=true"
```

**Practice exercise:** Add a negative API test for a missing name or null request body. Assert the HTTP status and make sure no new user appears.

## 7. JSON Schema contract testing

Read `api-tests/src/test/java/org/example/automation/UsersSchemaTest.java` and the schemas in `api-tests/src/test/resources/schemas/`.

A JSON Schema describes the **shape and allowed types** of JSON, rather than checking one hardcoded name. `user.json` covers a single user object; `users.json` covers the complete user array. The two tests validate `GET /api/users/1` and `GET /api/users` respectively:

```java
given().baseUri(baseUrl)
    .get("/api/users")
    .then().statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/users.json"));
```

The schema files live on the **test classpath**, so the validator can load them without absolute paths.

**Lesson from implementation:** An earlier attempt applied a schema matcher to `body("[0]", ...)`; REST Assured extracts that element as a Java Map, whereas the JSON Schema matcher expects JSON content. The working solution validates the **entire response body** against the array schema.

Run these together with the other API integration tests using `-Dintegration.tests=true`. If the API gains a required field, update the schema intentionally and review compatibility.

## 8. WireMock and external dependency simulation

The code in `api-tests/src/test/java/org/example/automation/ExternalApiClient.java` is a small Java `HttpClient` wrapper for `GET /external/status`. The client accepts a configurable base URI and timeout. `WireMockExternalApiTest.java` launches a local mock HTTP server on a **dynamic port** before each test:

```java
server = new WireMockServer(
    com.github.tomakehurst.wiremock.core.WireMockConfiguration
        .options().dynamicPort()
);
server.start();
```

The tests stub four responses:

| Scenario | WireMock behavior | Expected client result |
| --- | --- | --- |
| Success | `200 {"status":"ok"}` | Returns `"ok"` |
| Server error | HTTP `500` | Throws `IOException` |
| Unexpected payload | `200` with wrong status | Throws `IOException` |
| Slow response | Fixed delay 1500 ms | 250 ms client timeout throws `HttpTimeoutException` |

Example stub:

```java
server.stubFor(get(urlEqualTo("/external/status"))
    .willReturn(okJson("{\"status\":\"ok\"}")));
assertEquals("ok", client(Duration.ofSeconds(3)).fetchStatus());
```

`@AfterEach` stops the mock server. These tests do **not** call a live external API or require the Spring Boot app. This is why they can run in the offline Maven stage.

**Design lesson:** Mock an external service at its HTTP boundary when you want to test request/response handling, retries, failures, or timeouts reproducibly. Mockito mocks Java collaborators; WireMock mocks HTTP behavior.

## 9. Selenium smoke, Page Objects and CRUD

There are two Selenium styles in the repository:

- `UserManagerUiTest.java` is the original simple smoke test. It launches headless Chrome, searches for John Watson, checks a nonexistent query, and closes the driver in `finally`.
- `UserManagerCrudUiTest.java` extends `UiTestSupport` and uses `UserManagerPage`. This is the maintainable Page Object Model (POM) approach.

`UiTestSupport` uses JUnit `@BeforeEach` to launch `ChromeDriver`, chooses headless mode using `ui.headless`, sets implicit wait to zero, and opens the page. `@AfterEach` quits the browser. `@ExtendWith(UiFailureCapture.class)` enables screenshot capture on failures.

`UserManagerPage` wraps selectors and actions such as `search`, `enterUser`, `save`, `create`, `edit`, and `delete`. It uses `WebDriverWait` with a ten-second timeout for asynchronous UI state changes:

```java
void waitForUser(String name) {
    wait.until(d -> containsUser(name));
}
```

The delete method waits for a browser confirmation dialog, accepts it, then waits for the deleted row to disappear. The CRUD test creates a uniquely named user, searches, edits, deletes, and attempts cleanup in `finally`. Another test verifies duplicate-email rejection without adding a duplicate user.

Why POM? Tests should describe **business behavior** (`page.create(...)`) rather than repeat fragile CSS selectors everywhere. If the HTML changes, the Page Object is the first place to update.

```powershell
mvn -pl ui-tests clean test "-Dui.tests=true"
```

**Caution:** The legacy smoke test still manages its own driver and always runs headless; the shared setup and `ui.headless` switch apply to the POM-based tests. Tests depend on the seed user being present.

## 10. Playwright Java

Read `ui-tests/src/test/java/org/example/automation/PlaywrightUserManagerTest.java`. It uses `Playwright.create()`, launches Playwright's Chromium, navigates to the local app, creates a unique user, searches, edits and deletes it. It demonstrates both CSS selectors and accessible role-based locators:

```java
page.getByRole(AriaRole.BUTTON,
    new Page.GetByRoleOptions().setName("Edit " + name)).click();
```

Playwright automatically waits for many locator actions, while the test explicitly waits for important UI messages. The test closes the browser in `finally`, and captures a screenshot if the scenario throws an assertion error or runtime exception.

```powershell
mvn -pl ui-tests clean test "-Dplaywright.tests=true"
```

Selenium and Playwright are **alternative UI automation implementations**, not two drivers running within one test. Their flags are separate so CI can execute them independently. The Playwright test has one end-to-end CRUD scenario; it does not yet have a Playwright Page Object.

## 11. GitHub Actions CI pipeline

Open the workflow at repository root: `.github/workflows/automation-framework-ci.yml`.

The YAML has:
- **Trigger:** pushes to `feature/multi-module-test-framework` affecting `automation-parent/**` or the workflow file; manual `workflow_dispatch`.
- **Permissions:** `contents: read` (least-privilege checkout).
- **Concurrency:** newer runs on the same ref cancel older in-progress runs.
- **Runner:** `ubuntu-24.04`, pinned because the previous `ubuntu-latest` runner resolved to Ubuntu 26.04 and Playwright 1.56.x could not install Chromium there.
- **Java:** Temurin 21 via `actions/setup-java@v4` with Maven dependency caching.
- **Execution:** offline Maven reactor; package and launch Spring Boot; poll `GET /api/users/1`; run API integration; install Chrome; run Selenium; install Playwright Chromium; run Playwright.
- **Artifacts:** `actions/upload-artifact@v4` with `if: always()`, preserving Surefire reports, application log and failure screenshots for 14 days.

The readiness loop polls for up to 60 attempts, two seconds apart, rather than guessing how long startup takes. If startup fails, the workflow prints the application log.

### 11.1 How to read a run

Go to [Automation Framework CI](https://github.com/kkmahajan/IntelliJJavaSQL/actions/workflows/automation-framework-ci.yml), choose a run, open the job, and expand the failed step. Look for the **first** `[ERROR]` or nonzero process exit. The successful baseline is [run 41223588652](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41223588652): all workflow steps succeeded, including Playwright installation and execution.

### 11.2 Why 26 tests means separate invocations

The workflow invokes Maven several times. In the offline reactor, integration and browser tests are skipped by design. In the API stage, API and schema tests run; in the Selenium stage, Selenium runs; in the Playwright stage, Playwright runs. Counting **unique executed tests across stages** gives 12 Mockito + 4 WireMock + 4 API CRUD + 2 schema + 3 Selenium + 1 Playwright = **26**. WireMock also executes again in the API stage; therefore the **total number of test executions** is greater than 26. A `BUILD SUCCESS` in an offline stage alone does not prove integration coverage.

## 12. Reports, screenshots and troubleshooting

### 12.1 Where to find results

Each module writes Maven Surefire output to `<module>/target/surefire-reports/`. XML files are useful for CI and dashboards; text files show per-class results. UI failure screenshots go to `ui-tests/target/failure-artifacts/`. The CI workflow uploads these and `automation-parent/sample-app.log` as a downloadable artifact, even after a failed step.

`UiFailureCapture` implements JUnit's `AfterTestExecutionCallback`: if a Selenium POM test throws, it takes a screenshot **before** `@AfterEach` quits the driver. The Playwright scenario has its own screenshot-on-failure handler. Neither mechanism produces screenshots on successful runs.

### 12.2 Common problems

| Symptom | Likely reason | Action |
| --- | --- | --- |
| API test skipped | Missing `-Dintegration.tests=true` | Start app, then pass the flag |
| Selenium test skipped | Missing `-Dui.tests=true` | Start app, then pass the flag |
| Playwright test skipped | Missing `-Dplaywright.tests=true` | Install Chromium and pass the flag |
| Connection refused on port 8080 | Spring Boot app is not running | Start `mvn -pl sample-app spring-boot:run`; verify `/api/users/1` |
| Port 8080 already in use | Another process owns the port | Stop the process or configure app port and `app.baseUrl` together |
| Chrome DevTools Protocol version warning | Selenium 4.28.1 does not match the installed Chrome CDP version | Check whether tests actually fail; warning alone did not block verified runs |
| Playwright browser executable missing | Chromium was not installed for the current user/environment | Run Playwright CLI `install chromium` |
| Playwright says unsupported Ubuntu 26.04 | Runner/Playwright compatibility mismatch | CI pins `ubuntu-24.04` |
| Surefire `dumpstream` warning | A forked process wrote unexpected native output | Inspect `target/surefire-reports/*.dumpstream`; do not infer a cause without logs |
| UI test intermittent failure | Async rendering, stale data, or timing | Use explicit waits and unique test data; inspect screenshots and app log |

### 12.3 Recommended debugging order

1. Check whether the application is reachable with `Invoke-RestMethod http://localhost:8080/api/users/1`.
2. Reproduce **one module** locally using the exact flag used in CI.
3. Inspect `target/surefire-reports` and screenshots.
4. Identify whether the failure is setup, application, test assertion, browser, or environment.
5. Fix the smallest cause and rerun locally, then check the feature-branch GitHub Actions run.

## 13. How to extend the project

A practical development workflow:

1. Create or change an endpoint in `UserController`; put validation and business rules in `UserService`, not in the controller.
2. Add a Mockito unit test in `UserServiceTest` for success and failure behavior.
3. Add REST Assured assertions in `UsersApiTest` for HTTP status, response body and headers.
4. Update `user.json` or `users.json` if the API contract intentionally changes.
5. If the UI changes, update `index.html`, then centralize new Selenium selectors/actions in `UserManagerPage`.
6. Add a focused Selenium or Playwright scenario for a user-visible workflow, using unique test data and cleanup.
7. Run offline tests, start the app, run integration/browser suites, then push to the **feature branch** and inspect GitHub Actions.

Suggested future learning exercises (not yet implemented): Playwright Page Objects, test tags, environment profiles, accessibility checks, Testcontainers with a real database, retry policies, and CI test-result summaries.

## 14. Test inventory and limitations

| Class | Unique executed tests | Server required? |
| --- | ---: | --- |
| `UserServiceTest` | 12 | No |
| `WireMockExternalApiTest` | 4 | No; WireMock starts locally |
| `UsersApiTest` | 4 | Yes |
| `UsersSchemaTest` | 2 | Yes |
| `UserManagerUiTest` | 1 | Yes + Chrome |
| `UserManagerCrudUiTest` | 2 | Yes + Chrome |
| `PlaywrightUserManagerTest` | 1 | Yes + Playwright Chromium |
| **Total** | **26** | Across separate test stages |

The verified CI baseline is **26 unique tests passing across stages**, not a guarantee that every future run passes. The framework deliberately uses an in-memory application and local mock service; it is designed for learning and automation patterns, not production load, persistence, authentication, or transaction guarantees. UI tests mutate application state, so use only a disposable local test instance.

**Next reading:** Continue to [Phase 6–7: dual reports and Pages hosting](#15-phase-6-allure-and-extent-reports), or return to [README](../README.md) for a compact quick start, then inspect the Java classes linked by the paths above while running the matching test suite.

## 15. Phase 6: Allure and Extent reports

**Implemented and verified:** [CI run #41225043401](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41225043401) successfully ran the report-generation, feature-branch publication, and artifact-upload steps. Allure and Extent are generated together for the same workflow run.

### What changed in the code?

- Each test module (`sample-app`, `api-tests`, `ui-tests`) has the `io.qameta.allure:allure-junit5:2.29.1` test dependency and `src/test/resources/allure.properties` pointing to `target/allure-results`. The adapter records JUnit 5 test events as Allure results.
- The parent `pom.xml` includes the fourth Maven module, `reporting-tools`. This is a **report-generation utility**, not another test suite.
- `reporting-tools/src/main/java/org/example/automation/reporting/ExtentReportGenerator.java` uses **ExtentReports 5.1.2** and its `ExtentSparkReporter` to generate an HTML report from `TEST-*.xml` files produced by Maven Surefire.
- The CI workflow combines Allure result files from offline, API, Selenium, and Playwright stages in `target/combined-allure-results/`, then uses the Allure CLI to generate HTML at `reports/latest/allure/`.
- The same CI job generates Extent at `reports/latest/extent/index.html` and creates a simple `reports/latest/index.html` linking both reports.
- The reports are uploaded as a GitHub Actions artifact and committed under `automation-parent/reports/latest/` on the **feature branch**, with `[skip ci]` in the generated commit message.

### Reporting pipeline

```text
JUnit 5 test execution
  ├── allure-junit5 adapter ──> module/target/allure-results/
  │                                 └── combined results ──> Allure CLI ──> allure/index.html
  └── Maven Surefire ────────> module/target/surefire-reports/TEST-*.xml
                                    └── ExtentReportGenerator ──> extent/index.html
                                                 |
                                  reports/latest/index.html
                                                 |
                              GitHub artifact + feature-branch commit
```

**Why two reports?** Allure provides a JUnit-oriented results model with suites, trends, and detailed execution views. Extent Spark provides a separate HTML dashboard derived from Surefire XML. Both describe the same CI execution, but their internal representations and features differ.

**Important aggregation detail:** Maven tests are invoked more than once across CI stages. The Extent generator reads the Surefire XML files available at report-generation time; repeated runs of a class may overwrite earlier Surefire XML files. The Allure collection is copied between stages so earlier test results are preserved. Do not claim the two tools necessarily have identical raw record counts without checking the generated reports.

### Commands and files to inspect

```powershell
# Run from automation-parent after the tests have generated Surefire XML.
mvn -pl reporting-tools compile exec:java "-Dexec.mainClass=org.example.automation.reporting.ExtentReportGenerator" "-Dexec.args=. reports/latest/extent/index.html"

# In CI, Allure HTML is generated from the combined result directory:
# target/report-cli/node_modules/.bin/allure generate target/combined-allure-results --clean -o reports/latest/allure
```

To view the generated reports in GitHub source control, inspect `reports/latest/allure/index.html` and `reports/latest/extent/index.html`. GitHub's file viewer is **not** an HTML hosting service, so opening the repository HTML file normally shows its source rather than an interactive report.

### Troubleshooting

| Symptom | Likely cause / check |
| --- | --- |
| Allure report is empty | Verify the test module has `allure-junit5` and nonempty `target/allure-results/`; check CI collection steps |
| Extent has no executed tests | Verify `TEST-*.xml` exists in the module's `target/surefire-reports/` and tests were not all skipped |
| Only some UI results appear | Selenium and Playwright run separately; inspect the Allure collection steps and the Surefire XML overwrite behavior |
| HTML works locally but not in GitHub's code viewer | Use GitHub Pages rather than the repository's source viewer |
| Generated report commit triggers more CI runs | Confirm the bot commit uses `[skip ci]` and review the workflow trigger/path filters |

## 16. Phase 7: Publish both HTML reports using GitHub Pages

**Implemented in workflow:** `.github/workflows/automation-framework-ci.yml` now includes GitHub Pages deployment steps. **Deployment verification is separate:** enabling Pages in repository settings and observing a successful `Deploy HTML reports to GitHub Pages` step are required before claiming the URLs are live.

### Why GitHub Pages?

A report committed to GitHub is stored as a file, not served as a website. GitHub Pages hosts the generated HTML, JavaScript, CSS, and JSON assets so you can open interactive Allure and Extent reports directly in a browser without downloading artifacts.

### Repository settings

1. Open [Settings → Pages](https://github.com/kkmahajan/IntelliJJavaSQL/settings/pages).
2. Under **Build and deployment**, select **Source: GitHub Actions**.
3. Ensure Actions are enabled and that the workflow is allowed to deploy Pages. The workflow requests `pages: write` and `id-token: write`, plus `contents: write` for the existing report commit.
4. Push a change to `automation-parent/**` or the workflow on `feature/multi-module-test-framework`, or manually run the workflow.
5. In [Actions](https://github.com/kkmahajan/IntelliJJavaSQL/actions/workflows/automation-framework-ci.yml), verify the steps **Configure GitHub Pages**, **Upload HTML reports as Pages artifact**, and **Deploy HTML reports to GitHub Pages** succeed.
6. Visit **Settings → Pages** to confirm the live site address.

### How the workflow deploys the files

The report generation step creates `reports/latest/.nojekyll`, which prevents Jekyll processing of report assets. The workflow uses:

```yaml
permissions:
  contents: write
  pages: write
  id-token: write

# After generating both reports:
- name: Configure GitHub Pages
  if: success() && steps.reports.outcome == 'success'
  uses: actions/configure-pages@v5

- name: Upload HTML reports as Pages artifact
  if: success() && steps.reports.outcome == 'success'
  uses: actions/upload-pages-artifact@v3
  with:
    path: automation-parent/reports/latest

- name: Deploy HTML reports to GitHub Pages
  if: success() && steps.reports.outcome == 'success'
  id: pages
  uses: actions/deploy-pages@v4
```

The directory `automation-parent/reports/latest/` becomes the **root** of the Pages website. The expected URLs are:

| Page | Expected URL after successful deployment |
| --- | --- |
| Report dashboard | https://kkmahajan.github.io/IntelliJJavaSQL/ |
| Allure | https://kkmahajan.github.io/IntelliJJavaSQL/allure/ |
| Extent | https://kkmahajan.github.io/IntelliJJavaSQL/extent/ |

These are **expected**, not proof that deployment has completed. If GitHub Pages is configured with a custom domain or the deployment fails, the actual behavior will differ.

### CI flow after Phase 7

```text
Feature-branch push or manual dispatch
  -> Java build and offline tests
  -> Start Spring Boot and verify readiness
  -> API + Selenium + Playwright tests
  -> Generate Allure + Extent HTML
  -> Commit generated reports to feature branch [skip ci]
  -> Configure Pages -> Upload Pages artifact -> Deploy Pages
  -> Upload test logs and report artifacts
```

### Security and operational considerations

- GitHub Pages sites are generally **publicly viewable**. Never include secrets, tokens, personal data, or confidential test payloads in test names, logs, attachments, or screenshots.
- Pages is a repository-level site, not a separate private site for the feature branch. The workflow publishes from the feature branch but does **not** merge into or change `master`.
- The current pipeline commits generated report assets into source control **and** deploys them to Pages. This is intentional for the requested implementation, but generated HTML can make repository history large over time.
- If Pages deployment fails, check the GitHub Actions job and repository Pages settings before changing Java tests. The report-generation and Pages-deployment stages are distinct.

## 17. Phase 8: Service Desk business workflow and scalable testing

### 17.1 Business purpose and user journey

The application has grown from a generic user directory into a **Service Desk** demo for a small support team. Its purpose is to model a real sequence of customer-support activities that can be tested at the unit, API, and browser layers.

**Example scenario:** A customer cannot complete checkout. A support representative creates a ticket with the title "Checkout fails", records a description, sets priority to HIGH, and assigns the ticket to John Watson. When an agent starts investigating, the status changes from OPEN to IN_PROGRESS. Once the issue is fixed, the agent marks the ticket RESOLVED. A supervisor filters the board by HIGH priority and OPEN status to identify urgent unstarted work.

The UI at `http://localhost:8080` has two sections:

- **Team members:** The original user directory is preserved, with create/search/edit/delete. A seeded John Watson (user ID 1) is available as an assignee.
- **Support tickets:** A form accepts title, description, priority and assignee. Ticket cards show the issue number, description, priority, current status, and assignee. Users can edit, delete, start work, resolve, and filter by status or priority.

The interface uses accessible labels, status announcements and semantic ticket cards. JavaScript calls the same REST API that REST Assured tests exercise, making the UI and API paths consistent.

### 17.2 Domain model and business rules

`TicketController.Ticket` is a Java record with six fields:

| Field | Type | Rule |
| --- | --- | --- |
| `id` | int | Server-generated, monotonically increasing |
| `title` | String | Required, trimmed, 1–120 characters |
| `description` | String | Optional, trimmed, maximum 2,000 characters |
| `priority` | String | LOW, MEDIUM, HIGH (case-insensitive input) |
| `status` | String | OPEN, IN_PROGRESS, RESOLVED |
| `assigneeId` | Integer | Optional; if supplied, must identify an existing user |

**State machine:**

```text
Ticket created
      |
      v
    OPEN  -- Start work --> IN_PROGRESS -- Resolve --> RESOLVED
      |                         |                       |
      +--- same status ---------+---- same status ------+
```

Backward moves or skipping directly from OPEN to RESOLVED return **409 Conflict**. Invalid status values return **400 Bad Request**. Missing ticket IDs return **404 Not Found**. Updating ticket details preserves the current workflow status.

This is deliberately a small state machine. The app does **not** implement authentication, authorization, SLAs, comments, attachments, history, email notifications, or persistent storage.

### 17.3 Layered implementation

```text
Browser (static/index.html) or REST Assured client
                   |
          TicketController
      HTTP routes + request records
                   |
             TicketService
      validation, filtering, transitions
                   |
             TicketRepository
       ConcurrentHashMap + ID counter
                   |
            in-memory tickets

TicketService --> UserRepository (assignee validation)
```

New files:

- `sample-app/src/main/java/org/example/automation/TicketController.java`: REST endpoints, request/response records and status patching.
- `sample-app/src/main/java/org/example/automation/TicketService.java`: business validation, status transitions and filtering.
- `sample-app/src/main/java/org/example/automation/TicketRepository.java`: in-memory persistence abstraction with sorted listings and ID generation.
- `sample-app/src/main/resources/static/index.html`: original user management UI plus the support ticket board.

**Why separate layers?** A new persistence backend can be introduced without rewriting controller routes; business rules can be unit-tested without a server; the same API can support multiple frontends. The repository currently uses a concrete `TicketRepository` class and an in-memory map—this is a learning-scale architecture, not a production transaction model.

### 17.4 API specification

| HTTP | Endpoint | Success | Important errors |
| --- | --- | --- | --- |
| GET | `/api/tickets` | 200, JSON array; optional `status` and `priority` filters | 400 for invalid filter |
| GET | `/api/tickets/{id}` | 200, JSON object | 404 |
| POST | `/api/tickets` | 201, created ticket and Location header | 400 |
| PUT | `/api/tickets/{id}` | 200, edited ticket; status unchanged | 400, 404 |
| PATCH | `/api/tickets/{id}/status` | 200, ticket with updated status | 400, 404, 409 |
| DELETE | `/api/tickets/{id}` | 204, no content | 404 |

**Example PowerShell request** (after starting the Spring Boot application):

```powershell
$body = @{ title="Checkout fails"; description="Customer cannot pay"; priority="HIGH"; assigneeId=1 } | ConvertTo-Json
$ticket = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/tickets -ContentType "application/json" -Body $body
$ticket
Invoke-RestMethod -Method Patch -Uri "http://localhost:8080/api/tickets/$($ticket.id)/status" -ContentType "application/json" -Body '{"status":"IN_PROGRESS"}'
Invoke-RestMethod -Method Patch -Uri "http://localhost:8080/api/tickets/$($ticket.id)/status" -ContentType "application/json" -Body '{"status":"RESOLVED"}'
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/tickets/$($ticket.id)"
```

To filter the queue:

```powershell
Invoke-RestMethod "http://localhost:8080/api/tickets?status=OPEN&priority=HIGH"
```

### 17.5 Expanded automated tests

The original 26 unique tests remain in the framework. New suites add:

- `sample-app/src/test/java/org/example/automation/TicketServiceTest.java`: Mockito unit tests for creation, normalization, optional fields, invalid titles and priorities, unknown assignees, invalid filters, filtered results, valid/invalid state transitions, status preservation, missing tickets and deletion. Parameterized cases expand the test count automatically.
- `api-tests/src/test/java/org/example/automation/TicketsApiTest.java`: REST Assured tests for create/read/delete, full status progression, forbidden transitions, update, filtering, bad assignees, invalid payloads, invalid filters and missing records.
- `ui-tests/src/test/java/org/example/automation/ServiceDeskUiTest.java`: Selenium scenarios for ticket creation, assignment, progression to RESOLVED, priority filtering and cleanup.

**Run locally:**

```powershell
cd automation-parent
mvn clean test                       # Offline unit and WireMock tests
mvn -pl sample-app spring-boot:run   # Keep running in terminal 1

# Terminal 2, in automation-parent:
mvn -pl api-tests test "-Dintegration.tests=true"
mvn -pl ui-tests test "-Dui.tests=true"
```

JUnit 5 discovers new `*Test` classes without editing a central test suite. The existing Allure JUnit 5 adapters automatically capture new tests, and the Extent generator discovers their Surefire XML results. CI continues to run the module-specific test stages.

### 17.6 How to add a new business feature and tests

1. **Define a user story and acceptance criteria.** Example: "As a supervisor, I want to filter open high-priority tickets so I can prioritize urgent work."
2. **Define the API contract** (route, request, response, errors). Keep backward compatibility where practical.
3. **Implement business rules in the service** and data access in the repository, not in the controller.
4. **Add unit tests** for positive, boundary, negative and state-transition behavior.
5. **Add REST Assured tests** against the running app, with unique test data and cleanup.
6. **Add UI automation** for the user-visible journey using stable element IDs, accessibility labels, explicit waits and cleanup. For a larger framework, extract ticket interactions into a dedicated Page Object as a next refactor.
7. **Run all suites** locally and in CI. Review Allure and Extent reports, not just the workflow's green status.
8. **Update documentation** with new business rules, API routes and test scenarios.

### 17.7 Scalability boundaries

The modular Maven reactor, independent test layers, and automatic report discovery support **adding more tests with limited configuration changes**. They do not imply unlimited load scalability. The current in-memory store resets at startup and does not provide database transactions, authentication, authorization, event history, or cross-process persistence. For a production-grade next phase, consider a database with Testcontainers, role-based access control, service contracts, test fixtures, and a dedicated ticket Page Object.


## Phase 9 — Detailed execution reporting and Playwright BDD

The test framework records **real execution evidence**, not only test pass/fail status. The same Allure JSON result files drive **Allure HTML** and **Extent Spark HTML**, keeping step names and attachments aligned.

### Business and execution flows

- **REST Assured:** `ApiExecutionFilter` captures each actual HTTP method/URL, request body, response status, response body and elapsed time. It creates nested Allure steps with JSON attachments; Extent displays those steps and attachments by reading the same Allure results. Authorization-related values in JSON bodies and common secret query parameters are redacted. Do not test with real customer data.
- **Selenium:** Existing JUnit and Page Object flows are preserved. `UserManagerPage` and `ServiceDeskTicketPage` now record navigation, form values, selections, clicks and observed status messages as Allure steps. `UiFailureCapture` attaches a failure screenshot.
- **Playwright BDD:** The former JUnit Playwright CRUD test is now a Cucumber 7 scenario in `ui-tests/src/test/resources/features/user-management.feature`. Java step definitions in `ui-tests/src/test/java/org/example/automation/bdd/PlaywrightSteps.java` perform the **same** create, search, edit, delete journey. Hooks launch/close Chromium, create unique user data, clean up on failure, and attach screenshots. Gherkin steps and underlying browser actions appear in Allure, along with user-API request and response status events.
- **Extent:** `reporting-tools/.../ExtentReportGenerator.java` now parses `target/combined-allure-results/*-result.json`, including nested steps and attachments, rather than flattening Surefire XML into a single pass/fail line. The report copies screenshots to `extent/attachments`.

### How to execute

From `automation-parent`, use a running sample app at `http://localhost:8080`:

```powershell
mvn clean test "-Dcucumber.filter.tags=@offline"
mvn -pl api-tests test "-Dintegration.tests=true"
mvn -pl ui-tests test "-Dui.tests=true" "-Dcucumber.filter.tags=@offline"
mvn -pl ui-tests test "-Dplaywright.tests=true" "-Dcucumber.filter.tags=@playwright"
```

Install Playwright Chromium first if running locally. GitHub Actions installs it automatically and runs BDD Playwright separately from Selenium. The workflow merges Allure result files from offline, API, Selenium and Playwright stages, then regenerates Allure and Extent and deploys to GitHub Pages after a successful run.

### Where to inspect

- Dashboard: https://kkmahajan.github.io/IntelliJJavaSQL/
- Allure: https://kkmahajan.github.io/IntelliJJavaSQL/allure/
- Extent: https://kkmahajan.github.io/IntelliJJavaSQL/extent/
- Raw BDD JSON: `automation-parent/target/cucumber-report.json` in CI artifacts
- Failure screenshots: `ui-tests/target/failure-artifacts` and Allure attachments

### How to add another test

1. Define a real business acceptance criterion and expected outcome.
2. For API tests, use REST Assured in the existing `api-tests` module; the filter captures actual exchanges.
3. For Selenium, add reusable Page Object actions with descriptive `Allure.step` calls and JUnit assertions.
4. For Playwright, add a Gherkin scenario and implement each Given/When/Then in a Cucumber step-definition class.
5. Ensure generated test data is unique and cleaned up. Do not expose secrets or personal data in reports.
6. Run the appropriate test stage, inspect both report formats, then verify CI deployment.

**Reporting boundaries:** HTTP bodies are recorded for REST Assured requests; browser network events currently record method/URL/status for the user API, not all browser request/response bodies. Screenshots are evidence of browser state, not a replacement for assertions. Report output is public on GitHub Pages, so test data must be non-sensitive.
