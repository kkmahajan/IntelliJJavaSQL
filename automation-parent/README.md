# Java Multi-Module Test Automation Framework

A hands-on Java automation project built around a small **Service Desk** application. It demonstrates how to structure and execute unit, API, browser UI, and BDD tests in a Maven multi-module repository, then publish detailed execution evidence through **Allure**, **Extent Spark**, and a searchable GitHub Pages dashboard.

> **Branch and CI:** The CI workflow runs on pull requests and on pushes to `master` that touch `automation-parent/**`. Reports are deployed to GitHub Pages from `master` only; pull requests run every test without publishing.

## Project at a glance

| Capability | Implementation |
|---|---|
| Language / build | Java 21 target, Maven multi-module reactor |
| Sample application | Spring Boot 3.4.2, in-memory REST API and browser UI |
| Unit tests | JUnit 5, Mockito |
| API tests | REST Assured, JSON Schema validation |
| External dependency simulation | WireMock |
| Browser UI automation | Selenium 4, Page Object Model |
| Browser BDD automation | Playwright 1.56 + Cucumber 7 (Gherkin feature and Java step definitions) |
| Execution evidence | Allure 2.29.1 adapters, step annotations/runtime steps, screenshots and request/response attachments |
| Consolidated HTML reporting | ExtentReports 5.1.2, generated from structured Allure result JSON |
| CI | GitHub Actions, Ubuntu 24.04, Temurin Java 21 |
| Report hosting | GitHub Pages, with test-type filters |

**Useful links**

- [Latest CI workflow runs](https://github.com/kkmahajan/quality-engineering-framework/actions/workflows/automation-framework-ci.yml)
- [Latest published test dashboard](https://kkmahajan.github.io/quality-engineering-framework/)
- [Allure report](https://kkmahajan.github.io/quality-engineering-framework/allure/)
- [Extent report](https://kkmahajan.github.io/quality-engineering-framework/extent/)
- [Complete tutorial](docs/COMPLETE_TUTORIAL.md)
- [Interview preparation](docs/INTERVIEW_PREPARATION.md)
- [Java coding interview preparation](docs/JAVA_CODING_INTERVIEW_PREPARATION.md) — ten core Java topics, problem-solving patterns, complexity, SDET applications and exercises.

The published report links show the latest successfully deployed report. To inspect a particular historical run, open that run in GitHub Actions and download its report artifact.

---

## 1. Architecture

The framework lives in `automation-parent/` and is independent of the repository's original SQL/JDBC practice code.

```text
quality-engineering-framework/
├── .github/
│   └── workflows/
│       └── automation-framework-ci.yml
├── automation-parent/
│   ├── pom.xml                         # Maven parent / reactor
│   ├── sample-app/                     # Spring Boot demo application
│   │   └── src/
│   │       ├── main/java/              # Controllers, services, repositories
│   │       ├── main/resources/static/  # Service Desk web UI
│   │       └── test/java/              # JUnit + Mockito unit tests
│   ├── api-tests/
│   │   └── src/test/java/              # REST Assured, schema, WireMock tests
│   ├── ui-tests/
│   │   └── src/test/
│   │       ├── java/                   # Selenium page objects and tests
│   │       │   └── .../bdd/            # Cucumber Playwright step definitions
│   │       └── resources/features/     # Gherkin scenarios
│   ├── reporting-tools/
│   │   ├── src/main/java/              # Allure JSON → Extent Spark generator
│   │   ├── classify_results.py         # Adds test-type labels to Allure results
│   │   └── build_dashboard.py          # Builds searchable HTML dashboard
│   ├── docs/
│   │   ├── COMPLETE_TUTORIAL.md
│   │   └── INTERVIEW_PREPARATION.md
│   └── reports/latest/                 # Generated reports (CI output, git-ignored)
└── README.md                           # Original SQL/JDBC project overview
```

### Maven modules

| Module | Responsibility | Requires app running? |
|---|---|---|
| `sample-app` | Service Desk application and service-level unit tests | No |
| `api-tests` | REST API, schema and WireMock tests | Real app for integration tests; WireMock tests run independently |
| `ui-tests` | Selenium UI tests and Playwright Cucumber BDD | Yes |
| `reporting-tools` | Converts combined Allure JSON results into Extent HTML | No, but test result files must exist |

The parent POM sets the compiler release to Java 21 and manages the JUnit 5 version. Run Maven commands from `automation-parent/` unless a command says otherwise.

---

## 2. What the sample application does

The sample is a deliberately small, local **Service Desk** app. It is a test target and learning aid, not a production helpdesk.

Open the application at [http://localhost:8080](http://localhost:8080).

### User management

- List and search team members.
- Create, edit and delete users.
- Seeded user: **John Watson**, ID `1`, `john@example.com`.
- Reject invalid input with HTTP `400`; reject duplicate email with HTTP `409`.

### Support ticket management

- Create a ticket with title, description, priority and optional assignee.
- Edit ticket details, filter by status and priority, and delete tickets.
- Priorities: `LOW`, `MEDIUM`, `HIGH`.
- Ticket lifecycle: `OPEN → IN_PROGRESS → RESOLVED`.
- An assignee must refer to an existing user. Unassigned tickets are allowed.
- Invalid input returns `400`; a missing ticket returns `404`; forbidden status transitions return `409`.

### Ticket API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/tickets` | List tickets; optional `status` and `priority` filters |
| GET | `/api/tickets/{id}` | Retrieve one ticket |
| POST | `/api/tickets` | Create a ticket; returns `201` and `Location` |
| PUT | `/api/tickets/{id}` | Update ticket details |
| PATCH | `/api/tickets/{id}/status` | Advance ticket status |
| DELETE | `/api/tickets/{id}` | Delete ticket |

Example:

```bash
curl -i -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"title":"Checkout fails","description":"Payment button does nothing","priority":"HIGH","assigneeId":1}'
```

### User API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/users` | List users |
| GET | `/api/users/{id}` | Retrieve one user |
| POST | `/api/users` | Create a user |
| PUT | `/api/users/{id}` | Update a user |
| DELETE | `/api/users/{id}` | Delete a user |

The application stores data in memory. Users and tickets reset when the app restarts.

---

## 3. Prerequisites

- Git.
- Java 21 or newer (the project compiles to Java 21 bytecode).
- Maven 3.x, or IntelliJ IDEA's bundled Maven.
- Google Chrome for Selenium.
- Python 3 for the local report classification/dashboard scripts.
- Allure command-line tool only if you want to generate or serve Allure HTML locally.
- Playwright Chromium browser for the Playwright BDD scenario.

Check your tools:

```powershell
java -version
mvn -version
git --version
python --version
```

### Windows / IntelliJ bundled Maven

If Maven is not on PATH, the following is an example path for the IntelliJ IDEA Community Edition installation used during project development. Adjust it if your IntelliJ version or installation directory differs:

```powershell
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.1\plugins\maven\lib\maven3\bin\mvn.cmd"
& $mvn -version
```

In the commands below, replace `mvn` with `& $mvn` when using the bundled executable.

---

## 4. Get the project and build it

```powershell
git clone https://github.com/kkmahajan/quality-engineering-framework.git
cd quality-engineering-framework
git pull --ff-only
cd automation-parent

# Compile all modules and run the default test selection
mvn clean test
```

The opt-in integration and browser tests are intentionally guarded by Maven system properties. A default run is not the same as a full end-to-end run.

Build/package all modules without executing tests:

```powershell
mvn -B -ntp -DskipTests package
```

---

## 5. Start the application manually

Use **Terminal 1** from `automation-parent/`:

```powershell
mvn -pl sample-app spring-boot:run
```

Wait for Spring Boot to report that it has started. Keep this terminal open.

Open:

- [Service Desk UI](http://localhost:8080)
- [Users API](http://localhost:8080/api/users)
- [Seeded user](http://localhost:8080/api/users/1)
- [Tickets API](http://localhost:8080/api/tickets)

The application must be running before API integration tests, Selenium tests, or Playwright BDD tests.

---

## 6. Run tests manually

Run the commands from **Terminal 2**, also in `automation-parent/`, while the application is running where required.

### A. Unit and offline tests

```powershell
mvn clean test
```

This runs the normal Maven test selection. Integration and browser tests guarded by `integration.tests`, `ui.tests`, and `playwright.tests` are skipped unless enabled. WireMock tests do not require the sample application.

### B. API integration tests

```powershell
mvn -pl api-tests test "-Dintegration.tests=true" "-Dapp.baseUrl=http://localhost:8080"
```

Coverage includes user and ticket endpoints, CRUD, validation, ticket status transitions, filtering, not-found cases and schema checks. The REST Assured filter records the actual HTTP method/URL, request body, response status/body and duration as Allure evidence. Sensitive JSON fields and common secret query parameters are redacted by the filter; still use synthetic test data only.

### C. Selenium UI tests

```powershell
mvn -pl ui-tests test "-Dui.tests=true" "-Dapp.baseUrl=http://localhost:8080" "-Dui.headless=true"
```

Coverage includes user search and CRUD, duplicate-email feedback, ticket creation/assignment, ticket status progression and priority filtering. Selenium uses Chrome and Page Objects. Set `-Dui.headless=false` for tests using the shared UI test support if you want to watch the browser.

### D. Playwright + Cucumber BDD

Install the Playwright Chromium browser once:

```powershell
mvn -pl ui-tests dependency:build-classpath "-Dmdep.outputFile=ui-tests/target/playwright-classpath.txt"
$classpath = Get-Content "ui-tests/target/playwright-classpath.txt" -Raw
java -cp $classpath com.microsoft.playwright.CLI install chromium
```

Then execute the BDD scenario:

```powershell
mvn -pl ui-tests test "-Dplaywright.tests=true" "-Dapp.baseUrl=http://localhost:8080" "-Dcucumber.filter.tags=@playwright"
```

The feature is `ui-tests/src/test/resources/features/user-management.feature`. Its Java step definitions are under `ui-tests/src/test/java/org/example/automation/bdd/`. The scenario creates a unique user, searches for it, edits its name, verifies the update, deletes it, and verifies deletion. The Cucumber hook launches/closes Chromium, records browser user-API request/response method/URL/status events, attaches a final screenshot, and attempts cleanup.

### E. Run a single module

```powershell
mvn -pl sample-app test
mvn -pl api-tests test
mvn -pl ui-tests test
mvn -pl reporting-tools compile
```

For API and browser integration tests, add the relevant system property shown above and ensure the app/browser prerequisites are met.

### Test properties

| Property | Default | Purpose |
|---|---|---|
| `integration.tests` | `false` | Enables real-app API integration tests |
| `ui.tests` | `false` | Enables Selenium tests using the shared UI test support |
| `playwright.tests` | `false` | Enables the Playwright Cucumber hook |
| `app.baseUrl` | `http://localhost:8080` | Base URL of the sample app |
| `ui.headless` | `true` | Headless browser setting for supported browser tests |
| `cucumber.filter.tags` | Not set | Selects Cucumber scenarios, e.g. `@playwright` |

**Note:** The standalone legacy Selenium smoke test explicitly configures headless Chrome. The `ui.headless` property is honored by the shared Selenium support and Playwright hook.

---

## 7. Test coverage and design

| Layer | Main test classes / assets | What it verifies |
|---|---|---|
| Service unit | `TicketServiceTest` and user service tests | Business rules, validation, state transitions and repository interactions |
| API | `UsersApiTest`, `TicketsApiTest` | Real HTTP contract, CRUD, validation and lifecycle |
| Schema | `UsersSchemaTest` | Response structure against JSON Schema |
| External dependency | `WireMockExternalApiTest` | Client behavior against a controlled mock HTTP service |
| Selenium | `UserManagerUiTest`, `UserManagerCrudUiTest`, `ServiceDeskUiTest` | Browser UI and user/ticket workflows |
| Playwright BDD | `user-management.feature`, `PlaywrightSteps` | Business-readable create/search/edit/delete scenario |

### Framework design principles

- Keep test assertions in test/step-definition code and reusable browser interactions in Page Objects or helper methods.
- Generate unique data for create/update tests to avoid collisions.
- Clean up created records in `finally` blocks or Cucumber hooks.
- Use explicit waits for asynchronous UI changes.
- Keep API, UI and unit tests independently executable.
- Keep test evidence descriptive, but never place credentials or real customer data in report attachments.

---

## 8. Execution reports

The reporting pipeline uses the structured Allure result JSON as the common source for Allure and Extent.

### Report links

| Report | Link | Best for |
|---|---|---|
| Interactive dashboard | [Open dashboard](https://kkmahajan.github.io/quality-engineering-framework/) | Filter and search test results |
| Allure | [Open Allure](https://kkmahajan.github.io/quality-engineering-framework/allure/) | Individual test details, nested steps and attachments |
| Extent Spark | [Open Extent](https://kkmahajan.github.io/quality-engineering-framework/extent/) | Consolidated test execution and category view |

These URLs show the latest successfully deployed run. For a specific CI run, use its Actions artifact.

### Dashboard filters

The dashboard is generated from the combined Allure results and provides:

- **All** — every included executed test.
- **UI** — Selenium and Playwright together.
- **API** — API, schema and WireMock tests.
- **Selenium** — Selenium browser tests only.
- **Playwright** — Playwright Cucumber scenario(s).
- **Unit** — unit-level tests and other results not classified into the preceding groups.
- Search by test name and follow **View steps** to open its Allure detail.

Allure result tags and Extent categories are also populated by the report pipeline. The dashboard's classification is implemented in `reporting-tools/classify_results.py`; HTML generation is implemented in `reporting-tools/build_dashboard.py`.

### What the reports capture

- **API:** actual request method/URL, request body when present, actual response status/body and elapsed time.
- **Selenium:** descriptive browser interaction and verification steps from Page Objects, plus failure screenshots attached through the UI failure extension.
- **Playwright BDD:** Gherkin scenario steps, detailed browser actions, observed user API method/URL/status events, and screenshot attachment.
- **Extent:** nested steps and supported attachments are rendered by converting the same Allure result JSON.

**Evidence boundary:** The Playwright listeners currently capture user API request/response method, URL and response status—not every browser request/response body. Screenshots support diagnosis but do not replace assertions. REST Assured attachments are separate from browser network events.

### Generate reports locally

After executing the desired suites, combine their Allure result files. From `automation-parent/` in PowerShell:

```powershell
$results = "target/combined-allure-results"
New-Item -ItemType Directory -Force $results | Out-Null

@("sample-app", "api-tests", "ui-tests") | ForEach-Object {
    $source = Join-Path $_ "target/allure-results"
    if (Test-Path $source) {
        Get-ChildItem $source -File | Copy-Item -Destination $results -Force
    }
}

python reporting-tools/classify_results.py $results

# Allure CLI must be installed and available on PATH.
allure generate $results --clean -o reports/latest/allure

# Generate Extent Spark from the same structured Allure result JSON.
mvn -pl reporting-tools compile exec:java `
  "-Dexec.mainClass=org.example.automation.reporting.ExtentReportGenerator" `
  "-Dexec.args=. reports/latest/extent/index.html"

# Generate the filterable landing page.
python reporting-tools/build_dashboard.py $results reports/latest/index.html
```

Open `reports/latest/index.html`, `reports/latest/allure/index.html`, or `reports/latest/extent/index.html` in a browser. If you rerun tests, clear old module Allure result folders first to avoid mixing results from multiple executions. The CI pipeline handles collection and publishing automatically.

---

## 9. GitHub Actions CI

Workflow file: [`.github/workflows/automation-framework-ci.yml`](../.github/workflows/automation-framework-ci.yml)

[Open workflow runs](https://github.com/kkmahajan/quality-engineering-framework/actions/workflows/automation-framework-ci.yml).

### Pipeline stages

1. Check out the pushed or pull-request commit (read-only token).
2. Set up Temurin Java 21 and Maven dependency caching.
3. Run offline/default tests.
4. Package and start the sample Spring Boot app.
5. Wait for the users API readiness check.
6. Run API integration and schema tests.
7. Install Chrome and run Selenium tests.
8. Install Playwright Chromium and run the Playwright Cucumber scenario.
9. Collect Allure results and the Cucumber JSON report.
10. Classify results, generate Allure and Extent HTML, and build the dashboard.
11. Fail the build if any test category (Unit, API, Selenium, Playwright) executed zero tests, because a skipped suite would otherwise look green.
12. Upload report/test artifacts. On `master` only, a separate `publish` job (the only job with Pages permissions) deploys the report folder to GitHub Pages.

The workflow has a concurrency group that cancels superseded pull-request runs; runs on `master` are never cancelled mid-publish. Generated reports are not committed to the repository: they are uploaded as artifacts and deployed to Pages. CI artifacts are configured for 14-day retention.

### Inspect one CI run

1. Open the [workflow runs page](https://github.com/kkmahajan/quality-engineering-framework/actions/workflows/automation-framework-ci.yml).
2. Select a run.
3. Review each job step and its logs.
4. Download `automation-framework-reports-<run-number>` under **Artifacts**.
5. Inspect Surefire XML, combined Allure JSON, Cucumber JSON, screenshots (when captured), and generated HTML files included by that run.

The Pages links are rolling/latest reports, not immutable per-run URLs. Use the downloaded artifact for historical evidence.

---

## 10. Troubleshooting

| Symptom | Checks / resolution |
|---|---|
| `mvn` is not recognized | Use IntelliJ's bundled Maven executable as shown in Prerequisites, or add Maven to PATH. |
| App does not start / port 8080 is busy | Check the first terminal's Spring Boot log and stop the other process using port 8080. |
| API tests are skipped | Add `-Dintegration.tests=true`; verify the sample app is reachable at `app.baseUrl`. |
| Selenium tests are skipped | Add `-Dui.tests=true`; verify Chrome is installed and app is running. |
| Playwright scenario is skipped | Add `-Dplaywright.tests=true` and `-Dcucumber.filter.tags=@playwright`; verify Chromium is installed. |
| Chrome/Chromium executable missing | Install Chrome for Selenium; run the Playwright CLI install command for Playwright. |
| Browser test times out | Confirm the app readiness URL, inspect browser screenshot/failure artifacts, and check the test's explicit wait and locator. |
| Allure report is empty | Confirm each test module generated `target/allure-results` and that the files were copied into `target/combined-allure-results`. |
| Extent generation says no results | Run the test suites and combined-results collection first; verify the combined directory contains `*-result.json` files. |
| Dashboard has no tests | Run `classify_results.py` and `build_dashboard.py` against the populated combined-results folder. |
| GitHub Pages is not updated | Check that the workflow reached report generation and Pages deployment; verify Pages is configured to use GitHub Actions. |

---

## 11. Security and known limitations

This is an educational demo and test framework, not a production application.

- The sample app has no authentication, authorization, production database, durable persistence, SLA clock, audit trail or real customer integrations.
- Data is stored in memory and resets when the app restarts.
- Do not run destructive CRUD tests against production services or real customer data.
- Do not commit credentials, tokens, personal data, or secrets.
- GitHub Pages may be publicly accessible. Treat report names, logs, screenshots and attachments as public.
- The API reporting filter redacts selected common secret fields and query parameters; it is not a universal data-loss-prevention mechanism. Keep synthetic test data and review new request/response payloads before publishing.
- GitHub Actions and Pages availability/quotas depend on repository visibility and GitHub plan settings. Check GitHub billing for actual usage.

---

## 12. Learning resources inside this repository

- [Complete tutorial](docs/COMPLETE_TUTORIAL.md) — architecture, implementation walkthroughs, setup, execution, reporting and exercises.
- [Interview preparation](docs/INTERVIEW_PREPARATION.md) — project explanation, framework decisions and interview questions.
- [GitHub Actions workflow](../.github/workflows/automation-framework-ci.yml) — executable CI definition.
- [Gherkin feature](ui-tests/src/test/resources/features/user-management.feature) — Playwright BDD scenario.

## Contribution workflow

1. Work on a branch and open a pull request against `master`; CI runs the full suite on the pull request.
2. Keep tests isolated, deterministic and safe to rerun.
3. Add or update tests alongside application behavior changes.
4. Run the relevant local test stages and inspect reports.
5. Confirm the pull request's GitHub Actions workflow succeeds.
6. Keep documentation aligned with the implementation and its verified state.

Prefer pull requests over direct pushes to `master`.
