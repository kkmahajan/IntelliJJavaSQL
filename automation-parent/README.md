# Java Multi-Module Test Automation Framework

A realistic **Service Desk** demo application and extensible Java test automation project covering **JUnit 5, Mockito, REST Assured, JSON Schema, WireMock, Selenium Page Object Model, Playwright Java, GitHub Actions CI, Allure and Extent reporting**. The framework is developed in `feature/multi-module-test-framework` and isolated under `automation-parent/`; it does not replace the repository root Maven project.

**Start here:** [Complete step-by-step tutorial](docs/COMPLETE_TUTORIAL.md) — architecture, every test layer, code walkthroughs, setup, CI, debugging, and exercises.

**Preparing for interviews?** [Interview preparation and resume summary](docs/INTERVIEW_PREPARATION.md) — a 60-second project introduction, architecture overview, contributions, 12 interview Q&As, verified test results, and resume-ready bullet points.

## Architecture

```text
automation-parent/                  Maven reactor (Java 21)
├── sample-app/                     Spring Boot Service Desk (users + tickets)
│   └── UserController → UserService → UserRepository → InMemoryUserRepository
├── api-tests/                      REST Assured, JSON Schema, WireMock
├── ui-tests/                       Selenium POM, Playwright Java
├── reporting-tools/                 Extent Spark HTML generator
├── reports/latest/                 Generated Allure + Extent reports (after CI)
├── docs/COMPLETE_TUTORIAL.md       Detailed learning guide
└── README.md                       This quick start

.github/workflows/automation-framework-ci.yml   GitHub Actions workflow
```

| Module | Main test coverage | Requires running app? |
| --- | --- | --- |
| `sample-app` | User and ticket service unit tests | No |
| `api-tests` | 4 WireMock mock-HTTP tests | No |
| `api-tests` | User CRUD, ticket workflow API tests + JSON Schema | Yes |
| `ui-tests` | User CRUD and support ticket browser workflows | Yes + Chrome |
| `ui-tests` | 1 Playwright CRUD test | Yes + Playwright Chromium |

**Previously verified baseline:** [GitHub Actions run 41223588652](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41223588652) — all CI steps successful; **26 unique tests passed across separate stages**, with no test failures. Offline Maven runs intentionally skip integration and browser suites unless enabled.

## Application overview: Service Desk (users and support tickets)

This project now models a **small customer-support team's daily workflow**, rather than only a generic user CRUD example. It is a local learning app, not a production SaaS helpdesk.

### Business scenario

A customer reports that they cannot complete checkout. A support agent records a ticket, selects its priority, assigns it to a team member, begins investigating, and marks it resolved. A supervisor can filter the ticket board to identify open high-priority work.

**Main screens at `http://localhost:8080`:**

1. **Team members** — add, search, edit, and delete users. A seeded team member, John Watson (ID 1), can be assigned to tickets.
2. **Support tickets** — create a ticket with title, description, priority and optional assignee; edit details; advance status; delete a ticket; filter by status and priority.
3. **Workflow feedback** — accessible status messages, labeled controls, and buttons for `Start work` and `Resolve`.

**Lifecycle:** `OPEN → IN_PROGRESS → RESOLVED`. Skipping stages or moving backward is rejected with HTTP `409 Conflict`. Repeating the current state is permitted. Priority values: `LOW`, `MEDIUM`, `HIGH`. An assignee must refer to an existing user; unassigned tickets are allowed.

### Ticket API contract

| Method | Endpoint | Behavior |
| --- | --- | --- |
| GET | `/api/tickets` | List tickets; optional `?status=OPEN&priority=HIGH` filters |
| GET | `/api/tickets/{id}` | Get ticket; `404` if absent |
| POST | `/api/tickets` | Create ticket in `OPEN`; `201` and `Location` header |
| PUT | `/api/tickets/{id}` | Edit title, description, priority and assignee without changing status |
| PATCH | `/api/tickets/{id}/status` | Move to next status; `409` for forbidden transition |
| DELETE | `/api/tickets/{id}` | Delete ticket; `204` or `404` |

Example ticket creation:

```bash
curl -i -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"title":"Checkout fails","description":"Payment button does nothing","priority":"HIGH","assigneeId":1}'
```

Example lifecycle change:

```bash
curl -i -X PATCH http://localhost:8080/api/tickets/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

Ticket responses contain `id`, `title`, `description`, `priority`, `status`, and `assigneeId`. Validation returns `400` for missing/blank/oversized title, invalid priority/status, invalid assignee, or invalid filters. Tickets are stored in memory and reset when the application restarts.

### Extensibility and test organization

- **Controller → Service → Repository:** `TicketController` handles HTTP, `TicketService` validates input and business transitions, `TicketRepository` stores tickets. This separates business rules from transport and storage.
- **Unit tests:** `TicketServiceTest` tests validations, filtering, transitions, and updates using Mockito. No running server is needed.
- **API tests:** `TicketsApiTest` exercises a real Spring Boot HTTP server using REST Assured and unique test data. It cleans up created tickets.
- **UI tests:** `ServiceDeskUiTest` covers creation, assignment, status progression, filtering and cleanup in Selenium.
- **Existing suites preserved:** All user CRUD, WireMock, JSON Schema, Selenium, and Playwright coverage remains. JUnit discovery automatically includes the new `*Test` classes, and both Allure and Extent reporting use the existing module-level results.

**Important limitations:** This demo has no login, roles, persistence, pagination, SLA clock, audit trail, concurrency-safe ticket transactions, or real customer integrations. Do not claim these as implemented.

## Requirements

- Java 21+ (Maven compiler release is 21; CI runs Temurin 21).
- Maven 3.x and Git.
- Google Chrome for Selenium; Playwright Chromium for Playwright.
- Commands below run from **`automation-parent/`**.
- The app is disposable: it stores users in memory and resets on restart.

## Quick start (PowerShell)

```powershell
git switch feature/multi-module-test-framework
git pull --ff-only
cd automation-parent

# Run offline unit and WireMock tests (HTTP/browser tests intentionally skipped):
mvn clean test

# Terminal 1: start local app:
mvn -pl sample-app spring-boot:run
```

Visit `http://localhost:8080` (Service Desk UI) or `http://localhost:8080/api/users/1` (API). Keep the app running and open **terminal 2** in `automation-parent/`:

```powershell
# API integration + JSON Schema:
mvn -pl api-tests clean test "-Dintegration.tests=true"

# Selenium smoke + CRUD:
mvn -pl ui-tests clean test "-Dui.tests=true"

# Playwright: install Chromium once, then run the Playwright test:
mvn -pl ui-tests dependency:build-classpath "-Dmdep.outputFile=target/playwright-classpath.txt"
$classpath = Get-Content "ui-tests\target\playwright-classpath.txt"
java -cp $classpath com.microsoft.playwright.CLI install chromium
mvn -pl ui-tests clean test "-Dplaywright.tests=true"
```

If Maven is not on your Windows PATH, you can use IntelliJ's bundled executable instead:

```powershell
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.1\plugins\maven\lib\maven3\bin\mvn.cmd"
& $mvn clean test
```

Replace `mvn` with `& $mvn` in the other PowerShell commands as needed.

## Test configuration

| Property | Default | Meaning |
| --- | --- | --- |
| `integration.tests` | `false` | Opt in to REST Assured and JSON Schema tests |
| `ui.tests` | `false` | Opt in to Selenium tests |
| `playwright.tests` | `false` | Opt in to Playwright test |
| `app.baseUrl` | `http://localhost:8080` | Application URL used by tests |
| `ui.headless` | `true` | Browser window visibility for Selenium POM and Playwright |

The legacy Selenium smoke test always uses headless Chrome. For supported tests, add `"-Dui.headless=false"` to watch browser interaction.

## REST API

The sample app starts with user `1`: John Watson (`john@example.com`).

| Endpoint | Behavior |
| --- | --- |
| `GET /api/users` | List users |
| `GET /api/users/{id}` | Get user; `404` if absent |
| `POST /api/users` | Create; `201` and `Location` header |
| `PUT /api/users/{id}` | Update; `404` if absent |
| `DELETE /api/users/{id}` | Delete; `204` or `404` |

Invalid name/email returns `400`; duplicate email (case-insensitive) returns `409`. The HTML UI supports create, search, edit and delete.

## CI/CD and reports

[Automation Framework CI](https://github.com/kkmahajan/IntelliJJavaSQL/actions/workflows/automation-framework-ci.yml) runs on pushes to `feature/multi-module-test-framework` that change this project or the workflow, and can also be triggered manually. It does **not** target `master`.

The workflow uses **Ubuntu 24.04 + Java 21**, runs offline tests, starts the Spring Boot app with a readiness check, executes API tests, Selenium and Playwright separately, and uploads test artifacts even on failures.

- Local JUnit results: `sample-app/target/surefire-reports/`, `api-tests/target/surefire-reports/`, `ui-tests/target/surefire-reports/`.
- Failure screenshots: `ui-tests/target/failure-artifacts/`.
- CI artifact: `automation-framework-reports-<run number>` (14-day retention), including Surefire reports and application log.
- Historical successful CI baseline: [run #41223588652](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41223588652).

## Allure + Extent reports (Phase 6)

The same CI workflow generates **both report formats** from all test modules and publishes them together to `automation-parent/reports/latest/` on `feature/multi-module-test-framework`, in a bot commit marked `[skip ci]` to avoid recursive builds.

- [Report landing page](reports/latest/index.html) (available after the reporting-enabled CI run succeeds)
- [Allure HTML report](reports/latest/allure/index.html) — powered by `allure-junit5` adapters in every test module.
- [Extent Spark HTML report](reports/latest/extent/index.html) — built by `reporting-tools` from Surefire XML for every module.
- Both reports are also uploaded as GitHub Actions artifacts; `target/combined-allure-results/` contains combined raw Allure results.
- **GitHub Pages (browser viewing):** After enabling **Settings → Pages → Build and deployment → Source: GitHub Actions** and a successful Pages deployment, open the [report dashboard](https://kkmahajan.github.io/IntelliJJavaSQL/), [Allure](https://kkmahajan.github.io/IntelliJJavaSQL/allure/), or [Extent](https://kkmahajan.github.io/IntelliJJavaSQL/extent/). These links are expected deployment URLs, not guaranteed to be live until Pages succeeds.
- The workflow publishes the `reports/latest/` folder as the **site root**, in addition to committing reports to the feature branch and uploading CI artifacts. A `.nojekyll` file is generated for static assets.
- **Security:** Pages content may be public. Do not include credentials, tokens, customer data, or other sensitive information in test names, error messages, screenshots, or reports.
- GitHub's normal source-code viewer may display HTML as source rather than execute it; use the GitHub Pages URLs to view interactive HTML without downloading.
- The published reports are generated artifacts, not source files. Avoid manual edits.

**Generate Extent locally** after running the tests:

```powershell
mvn -pl reporting-tools compile exec:java "-Dexec.mainClass=org.example.automation.reporting.ExtentReportGenerator" "-Dexec.args=. reports/latest/extent/index.html"
```

**Generate Allure locally** after running tests and installing the Allure CLI:

```powershell
allure generate sample-app/target/allure-results api-tests/target/allure-results ui-tests/target/allure-results --clean -o reports/latest/allure
```

Local results may contain skipped or repeated executions if suites are run multiple times; the CI workflow collects its Allure results by stage.

## Learning path

| Phase | What was added | Detailed section |
| --- | --- | --- |
| 1 | Maven reactor, Spring Boot app, smoke tests | [Architecture and Maven](docs/COMPLETE_TUTORIAL.md#3-maven-reactor-and-module-structure) |
| 2 | CRUD REST API and browser UI | [Spring Boot and REST](docs/COMPLETE_TUTORIAL.md#4-spring-boot-sample-application-and-rest-design) |
| 3 | Mockito, WireMock, JSON Schema | [Unit tests](docs/COMPLETE_TUTORIAL.md#5-junit-5-and-mockito-unit-tests), [WireMock](docs/COMPLETE_TUTORIAL.md#8-wiremock-and-external-dependency-simulation) |
| 4 | Selenium POM and Playwright | [Selenium](docs/COMPLETE_TUTORIAL.md#9-selenium-smoke-page-objects-and-crud), [Playwright](docs/COMPLETE_TUTORIAL.md#10-playwright-java) |
| 5 | GitHub Actions and failure artifacts | [CI pipeline](docs/COMPLETE_TUTORIAL.md#11-github-actions-ci-pipeline) |

| 6 | Allure and Extent dual reports | [Reporting tutorial](docs/COMPLETE_TUTORIAL.md#15-phase-6-allure-and-extent-reports) |
| 7 | GitHub Pages HTML hosting | [Pages tutorial](docs/COMPLETE_TUTORIAL.md#16-phase-7-publish-both-html-reports-using-github-pages) |
| 8 | Real-world Service Desk ticket workflow and expanded regression suites | [Service Desk tutorial](docs/COMPLETE_TUTORIAL.md#17-phase-8-service-desk-business-workflow-and-scalable-testing) |

**Limitations:** This is a local educational test system, not a production app. It has no persistent database or authentication, and the in-memory implementation does not guarantee transactional concurrency. Avoid running its CRUD tests against production data.

For complete code explanations, setup variants, known warnings and troubleshooting, see **[the tutorial](docs/COMPLETE_TUTORIAL.md)**.


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
