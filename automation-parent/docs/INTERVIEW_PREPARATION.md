# Interview Preparation — Java Multi-Module Test Automation Framework

> **Project:** Java Multi-Module Test Automation Framework  
> **Repository:** [IntelliJJavaSQL — feature/multi-module-test-framework](https://github.com/kkmahajan/IntelliJJavaSQL/tree/feature/multi-module-test-framework/automation-parent)  
> **Stack:** Java 21, Maven, JUnit 5, Mockito, REST Assured, WireMock, JSON Schema, Selenium WebDriver, Playwright Java, Spring Boot, GitHub Actions, Allure, ExtentReports, GitHub Pages  
> **Verified CI baseline:** [Successful run #41225043401](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41225043401) — tests and dual-report generation/publication succeeded. GitHub Pages deployment was added later; verify its own CI step before claiming the live URLs work.

This document is intended for **interview preparation, project explanation, and resume writing**. For the full implementation walkthrough, see [COMPLETE_TUTORIAL.md](COMPLETE_TUTORIAL.md).

## 1. 60-second interview introduction

> I worked on building a Java-based, multi-module test automation framework that supports unit testing, API automation, UI automation, service mocking, and CI/CD integration.
>
> I used Maven to structure the project into three modules: a Spring Boot sample application, API automation tests, and UI automation tests.
>
> For API automation, I implemented REST Assured tests covering CRUD operations, HTTP status codes, positive and negative scenarios, and JSON Schema validation.
>
> I used JUnit 5 and Mockito to test business logic independently, and WireMock to simulate external API responses, including server errors, invalid responses, and timeouts.
>
> For UI automation, I implemented Selenium WebDriver using the Page Object Model with reusable methods, explicit waits, and automated browser management. I also added Playwright Java to automate end-to-end CRUD workflows.
>
> Finally, I integrated GitHub Actions to execute the suites on a clean environment, start the application, and generate **both Allure and Extent HTML reports** in the same run. I also configured GitHub Pages deployment so these reports can be hosted as web pages without downloading artifacts.
>
> The framework has 26 unique automated tests, all successfully verified in a GitHub Actions CI run.

## 2. Project architecture

```text
                  Maven Multi-Module Parent (Java 21)
                                |
            +-------------------+------------------+
            |                   |                  |
       sample-app           api-tests           ui-tests
       Spring Boot          REST Assured        Selenium POM
       REST API + UI        JSON Schema         Playwright Java
       Controller           WireMock            Browser CRUD
         -> Service
         -> Repository
         -> In-memory store
            |                   |                  |
            +-------------------+------------------+
                                |
                     GitHub Actions CI
                Build -> Start App -> API Tests
                 -> Selenium -> Playwright
                  -> Reports + Screenshots
```

**Why this architecture?** It separates the application under test from API and browser test code. The modules can be built and executed independently, while a parent Maven reactor manages Java and test plugin versions.

| Module | Responsibility | Representative files |
| --- | --- | --- |
| `sample-app` | Local application and isolated service unit tests | `UserController.java`, `UserService.java`, `UserRepository.java`, `InMemoryUserRepository.java`, `UserServiceTest.java` |
| `api-tests` | Real HTTP API tests, schema checks, mock HTTP scenarios | `UsersApiTest.java`, `UsersSchemaTest.java`, `WireMockExternalApiTest.java` |
| `ui-tests` | Selenium smoke and Page Object tests, Playwright browser flow | `UserManagerPage.java`, `UserManagerCrudUiTest.java`, `PlaywrightUserManagerTest.java` |
| CI workflow | Automated execution and failure artifacts | `.github/workflows/automation-framework-ci.yml` at repository root |

## 3. Key technical contributions

| Area | What was implemented | Why it matters |
| --- | --- | --- |
| Maven architecture | Three-module reactor targeting Java 21 | Separation of concerns and targeted builds |
| JUnit 5 | Tests, assertions, lifecycle hooks, parameterized cases | Consistent test execution |
| Mockito | Mocked repository in service unit tests | Fast testing without server or database |
| REST Assured | CRUD, response status/body/header, negative cases | Real API behavior verification |
| JSON Schema | Single-user and user-list response contracts | Detects unexpected response structure |
| WireMock | Local HTTP success, 500, invalid payload, timeout | Deterministic external dependency simulation |
| Selenium | Headless Chrome, Page Object Model, explicit waits | Maintainable browser automation |
| Playwright Java | Chromium CRUD scenario, role-based locators | Modern alternative browser automation |
| Test data | UUID-based unique users and cleanup | Reduced collisions between runs |
| GitHub Actions | Java setup, app readiness, API/UI stages, artifacts | Repeatable automated validation |
| Diagnostics | Surefire XML/text reports and failure screenshots | Faster debugging of CI failures |
| Dual reporting | Allure JUnit 5 adapter plus Extent Spark generated from Surefire XML | Two complementary HTML views of the same CI execution |
| Report publication | GitHub Actions artifacts, feature-branch commit, and configured GitHub Pages deployment | Reports are retained and can be hosted in a browser |

### REST endpoints under test

| Endpoint | Expected behavior |
| --- | --- |
| `GET /api/users` | `200`, list of users |
| `GET /api/users/{id}` | `200` or `404` |
| `POST /api/users` | `201` with `Location` header; `400` invalid input; `409` duplicate email |
| `PUT /api/users/{id}` | `200` or `404` |
| `DELETE /api/users/{id}` | `204` or `404` |

The local application seeds John Watson (`id=1`) and keeps data in memory, so the dataset resets on restart.

## 4. Verified automation results

| Test suite | Unique tests |
| --- | ---: |
| JUnit 5 + Mockito service tests | 12 |
| WireMock external API simulation | 4 |
| REST Assured API tests | 4 |
| JSON Schema contract tests | 2 |
| Selenium UI tests | 3 |
| Playwright Java UI test | 1 |
| **Total** | **26** |

**Result:** 26 unique tests passed and zero failed in the verified CI baseline. The workflow runs suites in **separate Maven invocations**; WireMock also executes again in the API stage. Tests skipped in the offline stage are intentionally enabled later with explicit flags. Do not describe this as 26 tests executing in a single Maven command.

## 5. Common interview questions and suggested answers

### Q1. Why did you choose a Maven multi-module framework?

I wanted clear separation between the application under test, API tests, and UI tests. The parent POM centralizes the Java compiler version and JUnit/Surefire configuration. Module-specific dependencies stay where they belong, and I can run focused commands such as `mvn -pl api-tests test` without running browser tests.

### Q2. Explain your Page Object Model implementation.

I created `UserManagerPage` to centralize selectors and reusable actions like create, search, edit, and delete. `UserManagerCrudUiTest` calls those business-oriented methods instead of duplicating browser interactions. `UiTestSupport` handles ChromeDriver setup/teardown and uses explicit waits to synchronize with UI updates. This improves readability and makes locator changes easier to maintain.

### Q3. What is the difference between Mockito and WireMock?

Mockito replaces **Java collaborators**, such as `UserRepository`, so I can unit-test `UserService` without starting Spring Boot. WireMock simulates an **HTTP server** and lets me verify how an HTTP client handles real response codes, invalid JSON content, and delayed responses. Both help isolate tests, but at different boundaries.

### Q4. How did you validate APIs with REST Assured?

I wrote tests that call the running Spring Boot API and assert HTTP status codes, JSON response fields, and headers. I covered create, read, update, delete, missing records, invalid input, and case-insensitive duplicate emails. I also added JSON Schema checks to verify the structure of both a single user response and a list response.

### Q5. How did you manage test data and isolation?

The application starts with a known sample record. CRUD tests generate unique identifiers, usually using UUIDs, to avoid duplicate names and email addresses. Tests attempt cleanup using `finally` blocks. I keep these tests against a disposable local application because they modify data.

### Q6. How did you reduce flaky Selenium tests?

I used explicit waits instead of fixed sleeps for loading and state changes, centralized UI actions in Page Objects, used unique test data, and added cleanup. I also capture screenshots for failures in Page Object-based Selenium tests. These choices reduce common sources of instability but do not eliminate every possible flaky failure.

### Q7. Why implement both Selenium and Playwright?

Selenium let me demonstrate established WebDriver automation with the Page Object Model and reusable browser setup. Playwright provided experience with modern browser automation, automatic locator waiting, and accessible role-based selectors. Both exercise the same local application, allowing comparison of their approaches.

### Q8. How does your CI/CD pipeline work?

GitHub Actions triggers on relevant pushes to the feature branch or through manual dispatch. It checks out code, configures Temurin Java 21, runs offline tests, starts the Spring Boot app, and polls an API endpoint until the app is ready. It then runs REST Assured/schema tests, Selenium tests, and Playwright tests in separate stages. Surefire reports, application logs, and failure screenshots are uploaded as artifacts.

### Q9. Describe a technical challenge you solved.

An initial CI run failed while installing Playwright Chromium. The default hosted Ubuntu runner was incompatible with the Playwright version in the project. I inspected the failing job logs, isolated the problem to the runner environment rather than the test itself, pinned the runner to Ubuntu 24.04, and verified that the next CI run completed successfully. This reinforced the importance of reproducible test environments and log-based diagnosis.

### Q10. What are the current limitations and next improvements?

The application uses an in-memory store rather than a real database, and it does not include authentication or transactional concurrency guarantees. Future improvements could include Testcontainers with a database, Playwright Page Objects, test tagging, environment profiles, richer reporting such as Allure, and accessibility testing. I distinguish these ideas from features already delivered.

### Q11. How did you implement Allure reporting?

I added the `allure-junit5` dependency and `allure.properties` to each test module. The JUnit 5 adapter writes raw result files during execution. Because API, Selenium, and Playwright suites run in separate CI stages, I collect their Allure result files into a shared directory, then generate a consolidated HTML report using the Allure CLI.

### Q12. How did you implement Extent reporting for all modules?

I created a separate Maven module named `reporting-tools` using ExtentReports and `ExtentSparkReporter`. Its Java entry point scans Surefire `TEST-*.xml` files from the three test modules, maps each test case to pass, fail, or skip, and produces a combined HTML report. It avoids coupling individual test classes to an Extent listener.

### Q13. Why did you choose both Allure and Extent?

Allure provides detailed JUnit-based test execution views, while Extent Spark provides a separate HTML summary from Surefire XML. Generating both in one CI job means the team can compare results from complementary perspectives without rerunning tests. I would clarify that the underlying raw record counts may differ if Surefire XML was overwritten by later stage invocations.

### Q14. How did you publish the HTML reports for browser access?

I configured the GitHub Actions workflow to generate both report directories, create a landing page, and upload `reports/latest/` as a GitHub Pages artifact using `actions/upload-pages-artifact` and `actions/deploy-pages`. GitHub Pages must also be enabled in repository settings with **Source: GitHub Actions**. The implementation is committed; I would verify a successful Pages deployment before claiming the public URLs are live.

### Q15. What is the difference between GitHub Actions artifacts and GitHub Pages?

Actions artifacts are downloadable build outputs with a retention period; they are useful for diagnostics and archiving. GitHub Pages serves static HTML and assets over HTTPS, letting users open interactive reports directly in a browser. Our workflow also commits generated HTML to the feature branch, but GitHub's code viewer is not equivalent to website hosting.

### Q16. How do you handle sensitive information in published reports?

I treat GitHub Pages as potentially public. I avoid including credentials, authorization headers, production data, customer identifiers, or secrets in test names, assertion messages, attachments, screenshots, and logs. Before enabling public reporting for a real system, I would audit the generated report contents and repository visibility.

### Q17. How are integration tests controlled?

JUnit `assumeTrue` checks system properties before running tests. REST Assured and schema tests use `-Dintegration.tests=true`, Selenium uses `-Dui.tests=true`, and Playwright uses `-Dplaywright.tests=true`. This allows a fast offline build while keeping integration and browser execution explicit.

### Q18. How do you debug a failing pipeline?

I open the GitHub Actions job and locate the first failing step. I inspect Surefire XML/text reports, app logs, and browser screenshots, then reproduce the relevant Maven module locally using the same flags. I separate setup/environment failures from application defects and assertion failures before making the smallest necessary change.

## 6. Resume-ready project entry

**Java Multi-Module Test Automation Framework | Java, REST Assured, Selenium, Playwright, GitHub Actions**

- Developed a modular Java automation framework using Maven and JUnit 5 to support unit, API, contract, and UI testing.
- Automated REST API CRUD workflows using REST Assured, including positive and negative validations, HTTP response assertions, and JSON Schema contract checks.
- Implemented Mockito-based unit tests and WireMock-based HTTP simulations to validate business logic, server errors, malformed responses, and timeout handling.
- Designed Selenium UI automation using the Page Object Model, reusable browser setup, explicit waits, and automated test data cleanup.
- Implemented Playwright Java browser automation for end-to-end user management workflows.
- Configured GitHub Actions CI to execute automated tests, start the Spring Boot application, provision browsers, and generate **Allure and Extent Spark HTML reports** in the same run.
- Configured GitHub Pages deployment for browser-accessible test reports, alongside feature-branch publication and downloadable CI artifacts.
- Successfully verified **26 unique automated tests with zero failures** in a GitHub Actions CI run.

## 7. How to present this project convincingly

Focus on the **engineering decisions**, not just a list of libraries:

1. **Architecture:** Why separate application, API and UI modules? How does the Maven reactor work?
2. **Isolation:** When should you use Mockito, WireMock or a real HTTP integration test?
3. **Maintainability:** Why Page Objects, explicit waits, UUID-based data and cleanup?
4. **CI ownership:** How does the pipeline start the app, provision browsers and collect evidence?
5. **Debugging:** What did the Ubuntu/Playwright compatibility issue teach you?

Be prepared to navigate to the actual Java files, explain at least one test from each layer, and run the matching Maven command. Avoid claiming that the project includes features such as a persistent database, authentication, or Testcontainers—those have not been implemented. Allure and Extent generation are verified; Pages deployment requires its own successful CI verification.

## 8. Further reading

- [Complete implementation tutorial](COMPLETE_TUTORIAL.md)
- [Project README](../README.md)
- [Verified successful dual-report CI run](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41225043401)
- [Report dashboard (expected after Pages deployment)](https://kkmahajan.github.io/IntelliJJavaSQL/)
- [Allure (expected after Pages deployment)](https://kkmahajan.github.io/IntelliJJavaSQL/allure/)
- [Extent (expected after Pages deployment)](https://kkmahajan.github.io/IntelliJJavaSQL/extent/)

## 9. Reporting architecture: whiteboard explanation

```text
JUnit 5 test suites
   |--> Allure adapter --> combined allure-results --> Allure HTML
   |--> Surefire XML -------------------------------> Extent Spark HTML
                                                      |
                                         reports/latest/index.html
                                                      |
                                  GitHub Actions artifacts + feature commit
                                                      |
                                      GitHub Pages deployment (configured)
```

**Interview talking point:** Report generation and report hosting are different responsibilities. I verified dual-report generation and publication in [CI run #41225043401](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41225043401). I then added GitHub Pages deployment, which needs a separate successful Pages job and repository configuration before its URLs are considered live.

### Practical demo checklist

1. Show `allure-junit5` in each module's `pom.xml` and `allure.properties`.
2. Open `reporting-tools/src/main/java/org/example/automation/reporting/ExtentReportGenerator.java` and explain Surefire XML aggregation.
3. Open `.github/workflows/automation-framework-ci.yml` and identify the Allure collection, Extent generation, artifact upload, and Pages deployment steps.
4. Open the latest successful workflow run and show the reporting steps.
5. If Pages has deployed successfully, demonstrate the dashboard and both reports in a browser.
6. Explain why publicly hosted reports must not contain secrets or confidential test data.
