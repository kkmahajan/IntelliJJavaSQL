# CI/CD Learning Log — Multi-Job Pipelines and Quality Gates

> **Status:** In progress — living document  
> **Started:** 28 November 2026  
> **Repository:** [kkmahajan/IntelliJJavaSQL](https://github.com/kkmahajan/IntelliJJavaSQL)  
> **Learning focus:** Lead SDET / Quality Engineering — CI architecture, parallel execution, artifact handling, reporting and release gates  
> **Working branch:** `learning/ci-cd-quality-gates`

This document captures the concepts, decisions, examples and implementation work from the learning sessions. Continue updating this file as the sessions progress. Keep `master` untouched until changes are reviewed and explicitly approved.

---

## 1. Learning objectives

By the end of this learning track, the repository should demonstrate that a QE/SDET can:

- Design a multi-job CI pipeline instead of running every suite sequentially in one job.
- Understand GitHub Actions job dependencies and runner isolation.
- Execute API, Selenium and Playwright suites independently and in parallel where appropriate.
- Transfer test results between jobs using workflow artifacts.
- Aggregate Allure results without losing attachments or creating filename collisions.
- Generate diagnostic reports even when tests fail.
- Enforce a reliable quality gate that reflects the outcome of all required suites.
- Separate diagnostic report publication from application deployment/release decisions.
- Apply least-privilege permissions, sensible concurrency and failure handling.

## 2. Current workflow baseline

The existing workflow is:

`.github/workflows/automation-framework-ci.yml`

Current characteristics reviewed during the session:

- One job: `test`.
- Runner: `ubuntu-24.04`.
- Java: 21.
- The sample Spring Boot application is started within the job and checked for readiness.
- API, Selenium and Playwright tests currently execute sequentially.
- Allure results are collected into a combined results directory.
- Allure and Extent HTML reports are generated.
- Reports are published to the repository and GitHub Pages.
- Test and failure artifacts are uploaded with an `always()` condition.

### Current sequential execution

```text
Checkout / Java setup
        |
Compile and offline tests
        |
Package and start sample app
        |
Wait for app readiness
        |
API tests
        |
Selenium tests
        |
Playwright tests
        |
Generate Allure + Extent reports
        |
Publish / deploy reports
        |
Upload diagnostic artifacts
```

### Why evolve it?

A single job is straightforward, but all suites run sequentially on one runner. This can increase total pipeline duration and makes the separation of suite-level responsibilities less explicit. A multi-job design can improve parallelism, isolation and failure visibility, provided the test environment and artifacts are handled correctly.

---

## 3. GitHub Actions fundamentals

| Concept | Meaning |
|---|---|
| Workflow | The complete automated pipeline defined in a YAML file |
| Trigger / event | What starts the workflow, such as `push` or `workflow_dispatch` |
| Job | A group of steps executed together on a runner |
| Runner | The machine/VM that executes a job |
| Step | An individual action or shell command inside a job |
| Artifact | A file or collection of files persisted from a job for later use/download |

Other workflow controls discussed:

- `permissions` controls the GitHub token permissions available to the workflow.
- `concurrency` can group related runs and optionally cancel an in-progress run when a newer run starts.
- `needs` expresses dependencies between jobs.
- `if: always()` allows a step or job to run even when an earlier dependency failed or was skipped, subject to the condition's other logic.

### Job dependency example

```yaml
jobs:
  build:
    runs-on: ubuntu-24.04
    steps:
      - uses: actions/checkout@v4
      - name: Build
        run: mvn -B -ntp clean package -DskipTests

  api-tests:
    needs: build
    runs-on: ubuntu-24.04
    steps:
      - uses: actions/checkout@v4
      - name: Run API tests
        run: echo "Replace with API test commands"

  selenium-tests:
    needs: build
    runs-on: ubuntu-24.04
    steps:
      - uses: actions/checkout@v4
      - name: Run Selenium tests
        run: echo "Replace with Selenium test commands"

  playwright-tests:
    needs: build
    runs-on: ubuntu-24.04
    steps:
      - uses: actions/checkout@v4
      - name: Run Playwright tests
        run: echo "Replace with Playwright test commands"

  reporting:
    needs: [api-tests, selenium-tests, playwright-tests]
    if: always()
    runs-on: ubuntu-24.04
    steps:
      - name: Aggregate results and generate reports
        run: echo "Replace with artifact download and report generation"
```

This is an architectural illustration, not a ready-to-merge workflow. Artifact upload/download, app startup, failure propagation and quality-gate logic still need to be implemented and tested.

---

## 4. Critical concept: runners are isolated

Each GitHub Actions job normally receives a fresh runner. A process started on one job's `localhost` is not available on another job's `localhost`.

For example:

- If the `build` job starts the sample app at `http://127.0.0.1:8080`, the `api-tests` job cannot use that same process.
- The Selenium and Playwright jobs also cannot access a process running on another job's runner.

Each test job therefore needs its own reachable test environment.

### Environment strategies

**Option A — Build/start independently in each test job**

Each test job checks out the code, builds or obtains the app, starts its own app instance, waits for readiness and runs its own suite.

- Easier first implementation.
- More repeated build work.
- Strong isolation between suites.

**Option B — Build once and share a runnable application artifact**

A build job packages the application and uploads it. Each test job downloads the artifact and starts its own local instance.

- Avoids repeating the application build.
- Requires a verified runnable artifact and explicit artifact transfer.
- Still gives each test job its own application process.

**Option C — Shared deployed test environment**

Deploy the application to an environment reachable by all test runners and configure each suite to use that URL.

- Avoids starting the app separately in every job.
- Requires environment lifecycle, test data isolation and concurrency controls.
- Parallel tests must not interfere with one another.

**Provisional learning-project direction:** Evaluate Option B first, after verifying that the packaged sample application can be downloaded and started reliably in a clean runner. Do not assume that the current local startup process is automatically reusable as an artifact.

---

## 5. Target multi-job architecture

The proposed logical design is:

```text
                    Build / Package
                          |
             +------------+------------+
             |            |            |
          API Tests   Selenium Tests  Playwright Tests
             |            |            |
             +------------+------------+
                          |
                Aggregate Test Results
                          |
                Generate Allure / Extent
                          |
                     Quality Gate
                          |
             Publish Diagnostic Reports
                          |
             Application Release Decision
```

Proposed job responsibilities:

| Job | Responsibility |
|---|---|
| `build` | Compile/package application and publish required build artifact |
| `api-tests` | Prepare isolated app environment, run API tests, upload results |
| `selenium-tests` | Prepare isolated app environment, run Selenium tests, upload results |
| `playwright-tests` | Prepare isolated app environment, run Playwright tests, upload results |
| `reporting` | Download suite results and generate combined reports |
| `quality-gate` | Evaluate all required test outcomes and return the correct workflow status |
| `deploy-reports` | Publish diagnostic reports according to reporting policy |

The final dependency graph and exact conditions must be designed carefully. A reporting job should be able to run after test failures, but its success must not conceal a failed test suite.

---

## 6. Quality gate principles

### Scenario discussed

| Job | Outcome |
|---|---|
| Build | Passed |
| API tests | Passed |
| Selenium tests | Failed |
| Playwright tests | Passed |
| Report generation | Succeeded |

Expected policy:

- Continue independent suites even if one suite fails.
- Collect and retain the available test results.
- Generate diagnostic reports where possible.
- Mark the overall quality gate as failed.
- Block the application release/deployment when the required suite has failed.

### Important distinction: report publication vs application deployment

These are separate decisions:

- **Diagnostic report publication:** Often useful even when tests fail, because the report helps investigate the failure.
- **Application deployment/release:** Should be blocked when a required quality gate fails, unless an explicitly designed and authorized exception policy applies.

Do not conflate publishing an HTML test report with deploying the application under test.

### Failure-handling principle

A successful reporting job does not mean the tests passed. The final workflow status must reflect the outcomes of all mandatory test jobs, not just whether report generation completed.

---

## 7. Artifact handling and collision prevention

Independent jobs cannot directly share their runner's local filesystem. They must upload results as workflow artifacts, and the reporting job must download those artifacts.

Illustrative upload step:

```yaml
- name: Upload API results
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: api-results
    path: automation-parent/api-tests/target/allure-results/
    if-no-files-found: warn
```

Use distinct artifact names for each suite:

- `api-results`
- `selenium-results`
- `playwright-results`

### Filename collision scenario

All three suites may produce an attachment named `screenshot.png`. If their files are merged into one flat directory, identically named files may overwrite one another.

Safer target structure:

```text
combined-results/
  api/
    screenshot.png
    <allure result files>
  selenium/
    screenshot.png
    <allure result files>
  playwright/
    screenshot.png
    <allure result files>
```

### Allure integrity warning

Allure result JSON files refer to attachments through attachment source names. Preserve the original result files and their referenced attachments together. Do not rename attachment files independently unless the corresponding references are updated consistently.

### Download strategy

Prefer downloading artifacts into separate suite directories (or otherwise preserving artifact boundaries) rather than flattening them into one directory. In particular, do not use `merge-multiple: true` blindly when different suites can contain identically named attachments.

An illustrative download pattern that preserves each artifact in its own subdirectory:

```yaml
- name: Download suite artifacts
  uses: actions/download-artifact@v4
  with:
    pattern: '*-results'
    path: combined-results
```

Validate the actual resulting directory structure and adapt the Allure aggregation command to that structure. The final aggregation step must place the required Allure result files and attachments into a valid combined results directory without collisions.

---

## 8. Knowledge checks completed

All three answers were correct.

| Question | User's answer | Correct principle |
|---|---|---|
| Selenium failed while other suites passed — should release proceed? | A | Retain reports, fail the quality gate and block application release |
| Same attachment name such as `screenshot.png` appears in multiple suite artifacts — what is the risk? | A | Files may overwrite one another when flattened/merged |
| API failed while Selenium and Playwright passed — what should happen? | A | Continue independent suites, generate reports and fail the overall quality gate |

**Key takeaway:** Parallel execution should improve speed and isolation without weakening the release gate or losing failure diagnostics.

---

## 9. Implementation checklist — pending

- [ ] Inspect the current Maven module build/package outputs.
- [ ] Verify a runnable sample application artifact can be created and started on a clean runner.
- [ ] Design the `build` job and artifact contract.
- [ ] Create independent API, Selenium and Playwright jobs.
- [ ] Ensure each suite has an isolated application/test environment.
- [ ] Upload each suite's Allure results and failure diagnostics with unique artifact names.
- [ ] Download results into separate directories in the reporting job.
- [ ] Combine Allure result files and attachments without filename collisions.
- [ ] Generate Allure and Extent reports when one or more test jobs fail.
- [ ] Implement an explicit quality gate that fails if any mandatory suite fails.
- [ ] Keep diagnostic report publication separate from application deployment.
- [ ] Review workflow permissions and concurrency behaviour.
- [ ] Run the workflow and inspect both successful and intentionally failing scenarios.
- [ ] Document execution time, failure behaviour and trade-offs.

---

## 10. Session continuation log

Append new concepts, code, decisions and validation results here as the learning progresses.

### Session 2 — 28 November 2026

**Completed so far**
- GitHub Actions workflow/job/runner/step/artifact fundamentals.
- Sequential vs multi-job pipeline architecture.
- Runner isolation and the `localhost` environment issue.
- `needs` and `if: always()` concepts.
- Quality gate behaviour after API or UI suite failures.
- Artifact transfer and duplicate attachment filename risks.
- Separation of diagnostic report publication from application release.

**Next topics**
1. Decide and validate the build artifact approach.
2. Design the exact job dependency graph.
3. Configure suite-level artifact upload and safe aggregation.
4. Design report generation and quality-gate conditions.
5. Implement the workflow on this branch and validate it.

### Further notes

_Add new notes below this line. Keep this document as the ongoing source of truth for this CI/CD learning track._
