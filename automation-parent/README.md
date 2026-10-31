# Java Multi-Module Test Automation Framework

A working local Java test automation project covering **JUnit 5, Mockito, REST Assured, JSON Schema, WireMock, Selenium Page Object Model, Playwright Java, and GitHub Actions CI**. The framework is developed in `feature/multi-module-test-framework` and isolated under `automation-parent/`; it does not replace the repository root Maven project.

**Start here:** [Complete step-by-step tutorial](docs/COMPLETE_TUTORIAL.md) — architecture, every test layer, code walkthroughs, setup, CI, debugging, and exercises.

**Preparing for interviews?** [Interview preparation and resume summary](docs/INTERVIEW_PREPARATION.md) — a 60-second project introduction, architecture overview, contributions, 12 interview Q&As, verified test results, and resume-ready bullet points.

## Architecture

```text
automation-parent/                  Maven reactor (Java 21)
├── sample-app/                     Spring Boot local REST API + HTML UI
│   └── UserController → UserService → UserRepository → InMemoryUserRepository
├── api-tests/                      REST Assured, JSON Schema, WireMock
├── ui-tests/                       Selenium POM, Playwright Java
├── docs/COMPLETE_TUTORIAL.md       Detailed learning guide
└── README.md                       This quick start

.github/workflows/automation-framework-ci.yml   GitHub Actions workflow
```

| Module | Main test coverage | Requires running app? |
| --- | --- | --- |
| `sample-app` | 12 JUnit/Mockito service unit tests | No |
| `api-tests` | 4 WireMock mock-HTTP tests | No |
| `api-tests` | 4 REST Assured CRUD tests + 2 JSON Schema tests | Yes |
| `ui-tests` | 1 Selenium smoke + 2 Selenium POM tests | Yes + Chrome |
| `ui-tests` | 1 Playwright CRUD test | Yes + Playwright Chromium |

**Verified baseline:** [GitHub Actions run 41223588652](https://github.com/kkmahajan/IntelliJJavaSQL/actions/runs/41223588652) — all CI steps successful; **26 unique tests passed across separate stages**, with no test failures. Offline Maven runs intentionally skip integration and browser suites unless enabled.

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

Visit `http://localhost:8080` (UI) or `http://localhost:8080/api/users/1` (API). Keep the app running and open **terminal 2** in `automation-parent/`:

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

## Learning path

| Phase | What was added | Detailed section |
| --- | --- | --- |
| 1 | Maven reactor, Spring Boot app, smoke tests | [Architecture and Maven](docs/COMPLETE_TUTORIAL.md#3-maven-reactor-and-module-structure) |
| 2 | CRUD REST API and browser UI | [Spring Boot and REST](docs/COMPLETE_TUTORIAL.md#4-spring-boot-sample-application-and-rest-design) |
| 3 | Mockito, WireMock, JSON Schema | [Unit tests](docs/COMPLETE_TUTORIAL.md#5-junit-5-and-mockito-unit-tests), [WireMock](docs/COMPLETE_TUTORIAL.md#8-wiremock-and-external-dependency-simulation) |
| 4 | Selenium POM and Playwright | [Selenium](docs/COMPLETE_TUTORIAL.md#9-selenium-smoke-page-objects-and-crud), [Playwright](docs/COMPLETE_TUTORIAL.md#10-playwright-java) |
| 5 | GitHub Actions and failure artifacts | [CI pipeline](docs/COMPLETE_TUTORIAL.md#11-github-actions-ci-pipeline) |

**Limitations:** This is a local educational test system, not a production app. It has no persistent database or authentication, and the in-memory implementation does not guarantee transactional concurrency. Avoid running its CRUD tests against production data.

For complete code explanations, setup variants, known warnings and troubleshooting, see **[the tutorial](docs/COMPLETE_TUTORIAL.md)**.
