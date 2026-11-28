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


### Session 2 — continued: build artifact strategy, readiness and retries

**Build once, test in parallel**

We discussed a build job that packages the sample application once and uploads a runnable artifact. API, Selenium and Playwright jobs can each download the same binary and start their own local application process.

Important points:
- Sharing a JAR does not share a running process or a runner's `localhost`.
- Each test job still needs its own application instance (unless using a separately managed shared test environment).
- Verify the actual JAR filename, executable packaging, startup command and health endpoint before relying on illustrative YAML.
- The sample module uses Spring Boot 3.4.2. The proposed artifact name `sample-app-1.0-SNAPSHOT.jar` was provisional and has not yet been verified by a clean build.

**Artifact failure nuance**

The correct response depends on where the failure occurs:
- If the central build job fails to create/upload the required artifact, all test jobs that depend on that build should be skipped.
- If the build succeeded but only the API job's individual artifact download fails, Selenium and Playwright should still proceed if their own downloads succeed. Jobs use independent runners; one job's download failure does not automatically invalidate another job's download.
- Never silently fall back to a stale artifact or rebuild without recording the failure.

The user's reasoning emphasized that a build failure should trigger investigation rather than allowing the pipeline to proceed with an uncertain binary. The key refinement is to distinguish a shared upstream artifact failure from an isolated per-job download failure.

**Readiness checks: polling rather than fixed sleep**

Avoid relying on a fixed delay such as `sleep 120`. Prefer polling a health/readiness endpoint at a bounded interval, with:
- A defined maximum wait.
- A short polling interval.
- A clear success message when ready.
- A non-zero exit when the timeout expires.
- Application logs and diagnostic output on failure.

An illustrative shell loop was discussed using a 90-second maximum wait and 3-second polling interval. It referenced `/actuator/health` as an example only; confirm that the sample app exposes this endpoint before adopting it.

**Retry strategy and flaky tests**

Retries should be limited and classified rather than applied indiscriminately:
- Transient network/runner issues may justify a bounded retry.
- Application startup failures should be diagnosed and may justify a controlled startup retry.
- Assertion failures and incorrect API responses should not be hidden by automatic retries.
- Browser crashes may justify a limited retry with diagnostic capture.

A test that fails initially and passes on retry is still a flaky-test signal. Preserve first-attempt failure information, retry outcome, test name, duration, environment/browser details, screenshots and traces where available. Do not automatically treat a retry pass as proof of a clean run.

**Knowledge checks completed in this continuation**

| Topic | User's answer | Result / principle |
|---|---|---|
| Build artifact failure | A | Correct core principle: do not proceed with an unknown or missing build artifact; investigate. Refine by distinguishing central artifact production failure from one job's isolated download failure. |
| Readiness check after app starts at 90 seconds but timeout is 60 seconds | B | Correct: bounded polling, sensible timeout and diagnostics on failure. |
| 100 Playwright tests: 97 pass first try, 2 fail consistently, 1 passes only on retry | B | Correct: fail the quality gate, retain reports, distinguish deterministic failures from the flaky test. |

**Newly completed concepts**
- Build-once / test-in-parallel artifact pattern.
- Failure isolation across dependent jobs.
- Bounded readiness polling and startup diagnostics.
- Retry classification and flaky-test visibility.
- Quality gate should not be green merely because a retry eventually passed.

**Next learning topics**
1. Test parallelism: splitting suites, workers, shards and execution time.
2. Resource contention: browser instances, CPU/memory, ports, test data and environment capacity.
3. Concurrency and isolation: avoiding collisions between parallel test runs.
4. Implement the verified build-artifact contract and independent test jobs on this learning branch.
5. Implement robust reporting and quality-gate conditions; validate both green and intentionally failing runs.

### Further notes

_Add new notes below this line. Keep this document as the ongoing source of truth for this CI/CD learning track._


### Session 2 — continued: resource isolation, parallelism levels and sharding

**Resource contention**

Parallelism is constrained by infrastructure capacity as well as test independence. Increasing worker count can increase throughput initially, but excessive concurrency may cause:
- CPU saturation and slow browser operations.
- Memory pressure, browser crashes or out-of-memory failures.
- Network contention and delayed requests.
- Database connection-pool exhaustion.
- External API rate limits (for example, HTTP 429 responses).

Tune concurrency using measured execution time, failure rate, CPU/memory use, browser stability and external-service limits. Do not assume that more workers always means faster or more reliable execution.

**Test-data isolation and cleanup ownership**

A shared mutable record can create race conditions. For example, two parallel tests updating the same customer to different statuses can produce order-dependent results. The preferred fix is independent test data, not simply more retries or disabling tests.

Recommended approaches:
- Generate unique records/accounts per test, worker or execution.
- Use separate tenants, schemas or database fixtures where practical.
- Give each worker independent browser contexts and sessions.
- Assign mobile devices and ports explicitly when parallel execution requires them.
- Use unique temporary directories and resource names.
- Make cleanup ownership-aware: a test should delete only the resources it created.
- Apply bounded throttling when external systems impose rate limits.

An execution identifier can combine the CI run ID and worker ID, for example `45384443361-worker-2`, to make test resources traceable and unique. This is an illustrative naming pattern; use the actual workflow context and collision-safe naming in implementation.

**GitHub Actions concurrency**

A concurrency group controls overlapping workflow or job runs that share a group. Example:

```yaml
concurrency:
  group: automation-${{ github.ref }}
  cancel-in-progress: false
```

- `cancel-in-progress: false` avoids cancelling an already-running run when a newer run starts in the same group.
- `cancel-in-progress: true` allows a newer run to cancel an in-progress run in that group.
- Workflow concurrency does not isolate test data or tune Playwright workers inside a job.
- Cancellation policy should account for whether diagnostics, report aggregation or deployment steps need to complete.

**Three levels of parallelism**

1. **Workflow level:** Separate workflows for distinct triggers or purposes, such as fast pull-request checks and full nightly regression. Avoid duplicating complete test execution without a reason.
2. **Job level:** Run API, Selenium and Playwright as independent jobs. GitHub Actions jobs run in parallel unless dependencies such as `needs` serialize them. Each job has its own runner and must prepare its own reachable application/test environment.
3. **Test-runner level:** Use workers or shards to distribute tests within a suite. Playwright supports commands such as:

```bash
npx playwright test --shard=1/4
npx playwright test --shard=2/4
npx playwright test --shard=3/4
npx playwright test --shard=4/4
```

A GitHub Actions matrix can create one job per shard. Setting `fail-fast: false` allows other matrix jobs to continue when one shard fails, which can preserve diagnostic coverage. Shards still need independent test data and a deliberate report-aggregation strategy.

**Sharding trade-offs**

- Equal test counts do not guarantee equal execution durations. Distribute tests using historical durations when possible.
- The slowest shard determines the end-to-end suite completion time.
- More shards increase runner provisioning and artifact/reporting overhead.
- Introduce sharding only after measuring that the suite is a bottleneck and confirming test isolation.

**Recommended progression for this repository**

1. Build once and run API, Selenium and Playwright in separate jobs.
2. Measure each suite's duration and reliability.
3. Verify test-data isolation and resource ownership.
4. Introduce Playwright sharding only if it remains a bottleneck.
5. Aggregate Allure results and attachments without collisions.
6. Apply a quality gate after all required suites have completed.

**Knowledge checks completed**

| Scenario | User's answer | Correct principle |
|---|---|---|
| Eight Playwright workers reduce duration but cause intermittent failures due to a shared test user | B | Fix data isolation first; then tune concurrency against stability and capacity. |
| Parallel API tests interfere because they share and delete the same customer | C | Use independent records and cleanup only resources created by each test. |
| 240-test Playwright suite needs to be split to reduce execution time | A | Shard the suite with isolated data and report aggregation; balance by duration, not only test count. |

**Newly completed concepts**
- Resource contention and concurrency tuning.
- Shared mutable test data, race conditions and ownership-aware cleanup.
- GitHub Actions concurrency groups and cancellation trade-offs.
- Workflow-, job- and test-runner-level parallelism.
- Playwright sharding and its balancing/reporting trade-offs.

**Next learning topics**
1. Compare matrix jobs, Playwright workers and suite-level jobs with a concrete cost/runtime example.
2. Review Allure's sharded result collection and safe aggregation requirements.
3. Inspect actual Maven module outputs and verify the runnable sample-app artifact.
4. Design and implement the build-artifact contract and independent test jobs on this learning branch.
5. Implement reporting and an explicit quality gate; validate both green and intentionally failing runs.
