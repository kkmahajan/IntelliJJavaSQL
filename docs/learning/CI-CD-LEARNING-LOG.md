# CI/CD Learning Log — Multi-Job Pipelines and Quality Gates

> **Status:** In progress — living document  
> **Started:** 28 November 2026  
> **Repository:** [kkmahajan/quality-engineering-framework](https://github.com/kkmahajan/quality-engineering-framework)  
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


### Session 2 — continued: executable Spring Boot artifact and runtime verification (28 November 2026)

**Local Maven environment**
- The standalone `mvn` command is not on the Windows PATH.
- Located and used IntelliJ IDEA Community Edition 2025.2.1's bundled Maven executable:
  `C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2025.2.1\\plugins\\maven-plugin\\lib\\maven3\\bin\\mvn.cmd`
- Verified Maven 3.9.16 and Java runtime 25.0.1. The project compiler configuration compiled sources with Java release 21.
- Build command, run from `automation-parent`:
  `mvn -B -ntp -pl sample-app -am clean package -DskipTests`
  (On this Windows machine, invoke the full `mvn.cmd` path with PowerShell's call operator `&`.)
- Initial reactor build succeeded and produced `sample-app/target/sample-app-1.0-SNAPSHOT.jar` (23,455 bytes).

**Packaging defect identified and fixed**
- The initial JAR was not executable: `java -jar sample-app/target/sample-app-1.0-SNAPSHOT.jar` returned `no main manifest attribute`.
- The existing `sample-app/pom.xml` declared `spring-boot-maven-plugin` with version `${spring-boot.version}`, but did not explicitly bind the `repackage` goal.
- Added an execution for the Spring Boot Maven plugin's `repackage` goal, retaining the existing Surefire plugin.
- After rebuilding, `java -jar` displayed the Spring Boot 3.4.2 startup banner. This confirms the JAR is now launching as a Spring Boot executable artifact. The complete startup log/ready message was not captured in the shared output, so endpoint verification is the stronger readiness evidence below.

**Runtime endpoint verification**
The application was running locally on port 8080. Both API checks returned HTTP 200:
- `GET http://localhost:8080/api/users` returned `[{"id":1,"name":"John Watson","email":"john@example.com"}]`.
- `GET http://localhost:8080/api/users/1` returned `{"id":1,"name":"John Watson","email":"john@example.com"}`.

This validates the current JAR's basic runtime behaviour and the two sample API endpoints. It does not yet prove that all dependencies/background components are healthy, nor does it replace a clean-runner CI validation.

**CI/CD implications**
- The candidate build artifact name is now verified locally as `sample-app-1.0-SNAPSHOT.jar`.
- Candidate startup command: `java -jar sample-app-1.0-SNAPSHOT.jar`.
- The build job can package this JAR once and upload it; each downstream API/Selenium/Playwright job must download the artifact and start its own application process because GitHub Actions jobs have separate runners and separate `localhost` environments.
- Before implementing the workflow, verify the exact artifact and startup behaviour on a clean Ubuntu runner, and define a bounded readiness poll against a confirmed endpoint. Do not assume `/actuator/health` exists; the two confirmed endpoints above can be used for a basic readiness check if appropriate.

**Validation status**
- [x] Locate and invoke bundled Maven.
- [x] Build the sample-app module successfully.
- [x] Identify actual JAR filename.
- [x] Correct Spring Boot executable packaging with `repackage`.
- [x] Launch the packaged JAR.
- [x] Verify `/api/users` and `/api/users/1` return HTTP 200 and expected data.
- [ ] Capture full startup/readiness log and confirm clean shutdown.
- [ ] Verify same artifact on a clean GitHub Actions Ubuntu runner.
- [ ] Define artifact upload/download contract and bounded readiness check.
- [ ] Implement independent test jobs, reporting aggregation and quality gate.

**Next step**
Inspect the current branch diff and Maven POM change, then design the build job's artifact upload and the downstream jobs' download/start/readiness steps. Keep all changes on `learning/ci-cd-quality-gates`; leave `master` untouched.


### Session 2 — continued: artifact provenance and remote-branch inspection (28 November 2026)

**Artifact provenance**

A build artifact should be traceable to the exact source revision and CI execution that produced it. Useful metadata includes:
- Git commit SHA.
- Workflow run ID (and run attempt where relevant).
- Artifact name and version.
- SHA-256 checksum.
- Build timestamp.

Traceability chain:

`Git commit → CI workflow run → Build artifact → Test results → Deployment`

**Release integrity principle:** deploy the exact artifact that passed the required tests. Do not rebuild during deployment, because a second build may produce a different binary from the one that was validated.

**Knowledge check**

Scenario: production has a defect and the deployed JAR came from an older commit even though the latest workflow passed. The user selected **B**: record and verify the commit SHA associated with every build artifact and deployment. Correct.

**Actual remote branch inspection**

Inspected the following files on `learning/ci-cd-quality-gates`:
- `.github/workflows/automation-framework-ci.yml`
- `automation-parent/sample-app/pom.xml`

Findings:
1. The current workflow still has one `test` job; API, Selenium and Playwright suites run sequentially in that job.
2. The current workflow packages the sample app, but starts it using `mvn ... spring-boot:run`; it does not yet exercise the packaged JAR.
3. The remote `sample-app/pom.xml` does not yet contain the locally tested Spring Boot `repackage` execution. The local fix therefore must be committed/pushed to the learning branch before the workflow can rely on the executable JAR.
4. The workflow's `push` and `pull_request` triggers currently target `master`. It has `workflow_dispatch`, which can be used to run it manually against the learning branch. Branch-trigger policy should be deliberately updated as part of the learning-branch implementation, without changing `master`.
5. The current workflow already generates Allure and Extent reports and uploads diagnostics with `if: always()`; the redesign must preserve this useful failure-diagnostic behaviour while separating suite jobs and report aggregation.

**Next implementation prerequisites**
- [ ] Commit and push the locally validated `sample-app/pom.xml` repackage change to `learning/ci-cd-quality-gates`.
- [ ] Re-fetch the remote POM and confirm the execution is present.
- [ ] Design a build job that packages, starts and smoke-tests the executable JAR before upload.
- [ ] Make API, Selenium and Playwright jobs download the same immutable artifact and start independent app processes.
- [ ] Preserve suite-specific result artifacts and the existing report/quality-gate behaviour.


### Session 2 — continued: local packaging fix confirmed on remote branch (28 November 2026)

The user confirmed the local `sample-app/pom.xml` change was pushed to the CI/CD learning branch.

**Remote verification**
- The actual branch ref in GitHub is `learning/ci-cd-quality-gates`. The shorter ref `ci-cd-quality-gates` does not exist; the user's wording referred to the existing learning branch.
- Re-fetched `automation-parent/sample-app/pom.xml` from `learning/ci-cd-quality-gates`.
- Confirmed the Spring Boot Maven plugin now has an explicit `repackage` execution.
- Remote POM blob SHA at verification: `9576514b62cef9707eb96e63826896e5a1844a23`.

**Status:** The executable-JAR packaging fix is now present on the remote learning branch. The workflow itself remains unchanged and still runs suites sequentially. Next, inspect the parent/API/UI Maven configuration and test/report output paths before making the first workflow refactor. Keep changes on `learning/ci-cd-quality-gates`; do not modify `master`.


### Session 2 — end-of-day checkpoint: artifact integrity, provenance and branch verification (28 November 2026)

This checkpoint consolidates the concepts covered and the repository work completed today. Earlier sections in this log remain the detailed record of the preceding lessons and local validation; this section records today's continuation and current hand-off state.

#### A. Concepts learned and knowledge checks

**1. Parallel artifact consumption and runner isolation**
- API, Selenium and Playwright jobs can each download the same uploaded build artifact in parallel.
- GitHub Actions jobs normally run on separate, fresh runners. Each job receives its own downloaded file copy and has its own filesystem and `localhost`.
- Each suite must start its own application process when using a locally hosted sample app. Sharing a JAR does not share a running process.
- If one job modifies its local JAR copy, it does not modify the copies downloaded by other jobs. Nevertheless, test jobs should treat build artifacts as immutable; mutation should be avoided and integrity can be checked.

Knowledge check: user answered **B** to the scenario where the API job accidentally modifies its downloaded JAR. Correct principle: the other jobs' copies remain unaffected; preserve immutable artifact handling and verify integrity where practical. The user also selected D; clarified that jobs do not share one application process or localhost.

**2. Artifact immutability and integrity**
- Build once, publish one validated artifact, and let each downstream job consume its own copy.
- Avoid rebuilding or modifying the binary in downstream test jobs.
- A SHA-256 checksum can verify that a downloaded file matches the file produced by the build job.

Illustrative commands:

```bash
sha256sum sample-app-1.0-SNAPSHOT.jar > sample-app.jar.sha256
sha256sum -c sample-app.jar.sha256
```

The checksum file must travel with the artifact, and verification must run in each consuming job before the JAR is started.

Knowledge check: user answered **B** to the local-copy modification scenario. Correct.

**3. Artifact provenance**
A reliable delivery pipeline should preserve the relationship:

`Git commit → CI workflow run → Build artifact → Test results → Deployment`

Useful metadata:
- Git commit SHA.
- Workflow run ID (and run attempt where relevant).
- Artifact name/version.
- SHA-256 checksum.
- Build timestamp.

Release integrity principle: deploy the exact artifact that passed the required tests. Do not rebuild during deployment, because a second build may produce a different binary from the one that was validated.

Knowledge check: production defect traced to a JAR from an older commit. User answered **B**: record and verify the commit SHA associated with every build artifact and deployment. Correct.

#### B. Repository inspection and implementation status

Repository: `kkmahajan/quality-engineering-framework`  
Working branch: `learning/ci-cd-quality-gates`

**1. User's local Spring Boot packaging fix**
- User confirmed the change was pushed to the CI/CD learning branch.
- Re-fetched `automation-parent/sample-app/pom.xml` from the remote branch.
- Verified the Spring Boot Maven plugin contains an explicit `repackage` goal execution.
- Remote POM blob SHA at verification: `9576514b62cef9707eb96e63826896e5a1844a23`.
- This confirms the locally tested executable-JAR packaging fix is now present remotely.

**2. Actual branch ref**
- The GitHub branch ref is `learning/ci-cd-quality-gates`.
- The shorter ref `ci-cd-quality-gates` returned GitHub 404 / no commit found. Use the full existing branch name for future reads and writes.

**3. Current workflow inspected**
File: `.github/workflows/automation-framework-ci.yml`

Verified current state:
- One `test` job on `ubuntu-24.04`, Java 21.
- Application is currently started with `mvn ... spring-boot:run`, not from the packaged executable JAR.
- API, Selenium and Playwright suites currently execute sequentially in that same job.
- Allure and Extent reports are generated.
- Test/failure artifacts are uploaded with `if: always()`.
- A separate Pages `publish` job depends on `test`.
- `push` and `pull_request` triggers currently target `master`; `workflow_dispatch` is enabled.
- The workflow has not yet been refactored. No workflow changes were made during this session.

#### C. Current status

Completed:
- [x] Explain how parallel test jobs consume independent copies of the same artifact.
- [x] Clarify that each job starts its own app process; jobs do not share localhost.
- [x] Explain artifact immutability and SHA-256 integrity checks.
- [x] Explain commit-to-artifact-to-deployment provenance.
- [x] Confirm the Spring Boot `repackage` fix exists on the remote learning branch.
- [x] Inspect and document the existing workflow architecture and trigger configuration.
- [x] Keep all GitHub documentation updates on `learning/ci-cd-quality-gates`; no changes to `master`.

Still pending:
- [ ] Inspect parent POM, API/UI module POMs, test execution flags and report output paths.
- [ ] Design the build job to package the JAR, start it, perform bounded readiness polling against a confirmed endpoint, capture diagnostics on failure, stop the process and upload only after successful smoke validation.
- [ ] Include the JAR, checksum and provenance metadata in the build artifact.
- [ ] Create independent API, Selenium and Playwright jobs that download and verify the same artifact, then each start their own app process.
- [ ] Upload suite-specific Allure results and failure diagnostics without flattening attachment directories.
- [ ] Aggregate results, generate Allure/Extent reports even after test failures, and implement an explicit quality gate.
- [ ] Validate successful and intentionally failing workflow runs on GitHub Actions.
- [ ] Deliberately decide how the learning branch should be triggered; do not change `master` trigger behaviour without explicit review.

#### D. Next session — exact starting point

1. Inspect the parent POM and API/UI module POMs, including Surefire configuration and test-selection properties.
2. Confirm where each suite writes Allure results, Cucumber JSON, Surefire XML and failure artifacts.
3. Draft the build job and artifact contract before splitting the test suites.
4. Keep implementation on `learning/ci-cd-quality-gates` and preserve the current reporting/diagnostic behaviour.

**End-of-day note:** The packaging fix is committed remotely and verified. The current CI workflow is still sequential; the next work is a careful, evidence-based refactor rather than an unverified wholesale replacement.


### Session 3 — Maven module boundaries, suite selection and report outputs (28 November 2026)

**Maven module structure verified remotely**
- Parent reactor: `automation-parent`, packaging `pom`, Java compiler release 21.
- Modules: `sample-app`, `api-tests`, `ui-tests`, `reporting-tools`.
- `api-tests` uses JUnit 5, REST Assured, JSON schema validation, WireMock and Allure JUnit 5.
- `ui-tests` contains Selenium JUnit tests plus Cucumber/Playwright tests. Both UI suites share the same Maven module but can and should be run in separate CI jobs.
- `reporting-tools` contains the Extent report generator and Python report-processing scripts.

**Important distinction**
A Maven module is a code/build boundary; a GitHub Actions job is an execution/isolation boundary. Selenium and Playwright share `ui-tests`, but separate CI jobs can still run them independently on separate runners. Each job must check out the code, set up Java/dependencies, download and verify the same app artifact, start its own app process, execute only its intended suite, and upload its own results.

**Suite selection verified from source**
- API tests use the JUnit assumption `Boolean.getBoolean("integration.tests")`; run with `-Dintegration.tests=true`. They read `app.baseUrl` (default `http://localhost:8080`).
- Selenium JUnit tests use `Boolean.getBoolean("ui.tests")`; run with `-Dui.tests=true`. They read `app.baseUrl` and default to headless Chrome.
- Playwright Cucumber hooks use `Boolean.getBoolean("playwright.tests")` and the Cucumber feature is tagged `@playwright @regression`; run with `-Dplaywright.tests=true -Dcucumber.filter.tags=@playwright`.
- The current workflow's Selenium command also passes `-Dcucumber.filter.tags=@offline`. The Selenium JUnit tests are selected by `-Dui.tests=true`; the `@offline` Cucumber filter does not select the Playwright-tagged feature.
- The initial reactor-wide offline test stage runs with integration/UI flags unset, so those gated suites are skipped by assumptions while ordinary unit tests can run. Keep a category-execution check so a mistyped flag cannot silently result in a green but unexecuted suite.

**Allure and diagnostics output paths verified**
- API Allure: `api-tests/target/allure-results`.
- UI Allure (both Selenium and Playwright): `ui-tests/target/allure-results`.
- Cucumber JSON: `ui-tests/target/cucumber-report.json`.
- Selenium failure screenshots: `ui-tests/target/failure-artifacts`.
- Both API and UI modules have `allure.properties` with `allure.results.directory=target/allure-results`.
- `classify_results.py` labels result JSON into API, Selenium, Playwright or Unit based on Allure tags and test identifiers; it also adds UI family labels for Selenium/Playwright.
- `verify_results.py` checks that required categories have at least one executed test, defaulting to Unit, API, Selenium and Playwright.

**CI design implication**
Because Selenium and Playwright share `ui-tests/target/allure-results`, separate jobs naturally isolate their filesystems. Each job should upload its results under a distinct artifact name (for example `selenium-results` and `playwright-results`). The reporting job should download them into separate directories first, then aggregate the result JSON and referenced attachments without flattening duplicate attachment filenames. Preserve the Cucumber JSON and Selenium failure-artifact directory as separate diagnostics.

**Current workflow design matrix**

| Job | Maven module / command intent | Required flag / selection | Result outputs |
|---|---|---|---|
| Build | `-pl sample-app -am package -DskipTests` | None | Executable sample-app JAR |
| API | `-pl api-tests test` | `-Dintegration.tests=true` | `api-tests/target/allure-results` |
| Selenium | `-pl ui-tests test` | `-Dui.tests=true`, exclude Playwright Cucumber tag | `ui-tests/target/allure-results`, `target/failure-artifacts` |
| Playwright | `-pl ui-tests test` | `-Dplaywright.tests=true -Dcucumber.filter.tags=@playwright` | `ui-tests/target/allure-results`, `target/cucumber-report.json` |

This is the verified design intent; exact command behaviour must be confirmed in GitHub Actions before treating the new pipeline as complete.

**Next**
- [ ] Design a reusable app-start/readiness/cleanup shell sequence for each test job.
- [ ] Decide whether to avoid repeating Maven compilation in the two UI jobs by using Maven caching or a prebuilt test bundle; do not share runner workspaces.
- [ ] Implement artifact upload/download and safe report aggregation.
- [ ] Test that each category executes and the quality gate correctly reflects failures.


### Session 3 — continued: checksum verification, provenance enforcement and complete quality gates (28 November 2026)

**Checksum verification**
- A downstream suite must verify the downloaded JAR against the checksum generated by the build job before executing it.
- If verification fails, stop before starting the application, fail that suite job and preserve the checksum output and artifact metadata.
- Do not generate a fresh checksum in the consumer job and compare the file to itself; that would not verify the build artifact's integrity.
- Do not silently fetch a different or latest JAR as a fallback.

Illustrative commands:
```bash
# Build job: generate and upload this checksum alongside the JAR
sha256sum sample-app-1.0-SNAPSHOT.jar > sample-app-1.0-SNAPSHOT.jar.sha256

# Consumer job: verify the downloaded JAR against the downloaded checksum
sha256sum -c sample-app-1.0-SNAPSHOT.jar.sha256
```

**Commit provenance verification**
- A matching checksum establishes that the downloaded bytes match the bytes associated with that checksum; it does not establish that the artifact came from the expected source revision.
- Compare artifact metadata's source commit SHA with the commit SHA expected by the current workflow run.
- If the values differ, reject the artifact before execution and preserve metadata/logs. Do not rebuild or substitute an older artifact silently.
- Maintain the chain: `Source commit → Build → Artifact → Test results → Deployment`.

**Independent suite behaviour**
- If API rejects its artifact because of a checksum or commit-provenance mismatch, API must stop and fail.
- Selenium and Playwright may continue if each independently downloaded artifact passes its own checksum and commit verification.
- This allows independent diagnostics without weakening artifact safety. A failure in one consumer job does not automatically invalidate another consumer's verified local copy.

**Required-suite reporting and quality gate**
- A required suite that could not execute because artifact verification failed must be reported as failed, with zero tests executed and the prerequisite failure recorded. It must not be reported as passed simply because no test assertion failed.
- Distinguish a required suite failure from an intentionally skipped/non-required suite.
- The final quality gate must pass only when every required suite completed successfully, required test categories actually executed, no blocking failures occurred, and required artifact integrity/provenance checks passed.
- A successful reporting job is not equivalent to a successful quality gate. Reporting should still aggregate available results and produce diagnostics where possible.

**Knowledge checks completed**
| Scenario | User's answer | Correct principle |
|---|---|---|
| Downloaded JAR checksum verification fails | C | Stop before execution; preserve diagnostics; do not trust or replace the artifact silently |
| Artifact metadata commit differs from current workflow commit | B | Reject the artifact; checksum alone does not establish correct source provenance |
| API rejects its artifact while Selenium and Playwright each verify theirs | A | Incorrect: independent verified suites should continue; overall gate still fails due to API |
| API could not execute due to commit mismatch; how to classify it | B | Failed required suite, zero tests executed, with artifact verification failure recorded |
| Final gate receives API failed/0 executed, Selenium and Playwright passed | C | Pass only if every required suite completed, required tests executed, and no blocking failures occurred |

**Next**
- [ ] Design the explicit job-result contract for each suite (execution status, executed test count, artifact verification status, result artifact availability).
- [ ] Draft the reporting and quality-gate dependency/condition logic, including failed and skipped jobs.
- [ ] Implement only after reviewing the current workflow and its trigger scope; keep changes on `learning/ci-cd-quality-gates` and leave `master` untouched.


### Session 4 — continued: suite-result contract and required execution validation (28 November 2026)

**Standard suite-result contract**

Each required suite should publish a machine-readable result record, including:
- Suite name and source commit.
- Artifact integrity and provenance verification outcomes.
- Execution status.
- Tests executed, passed and failed.
- Evidence upload status.
- A failure reason when a prerequisite or execution step prevents completion.

Illustrative completed API result:
```json
{
  "suite": "API",
  "sourceCommit": "abc123",
  "artifactVerified": true,
  "provenanceVerified": true,
  "executionStatus": "COMPLETED",
  "testsExecuted": 120,
  "testsPassed": 120,
  "testsFailed": 0,
  "evidenceUploaded": true,
  "failureReason": null
}
```

Illustrative Playwright result when runner provisioning prevents execution:
```json
{
  "suite": "PLAYWRIGHT",
  "sourceCommit": "abc123",
  "artifactVerified": true,
  "provenanceVerified": true,
  "executionStatus": "NOT_STARTED",
  "testsExecuted": 0,
  "testsPassed": 0,
  "testsFailed": 0,
  "evidenceUploaded": false,
  "failureReason": "RUNNER_PROVISIONING_FAILED"
}
```

**Execution status and test outcome are separate dimensions**
- `COMPLETED`: suite execution finished; its test outcome may still be pass or fail.
- `NOT_STARTED`: a prerequisite/infrastructure issue prevented execution.
- `SKIPPED`: intentionally excluded by the declared pipeline policy.
- `INCOMPLETE`: execution began but did not finish reliably.

A required suite marked `NOT_STARTED` is a blocking quality-gate failure, not a pass. An intentionally skipped suite is acceptable only when the pipeline policy explicitly marks it non-required for that run.

**Reporting and evidence are separate from test outcome**
- Reporting may successfully generate Allure/Extent reports from available evidence even when a suite failed or did not start.
- A reporting job's success must not override suite failures.
- If mandatory evidence upload or download fails, preserve the exact failure boundary: tests may have passed, but the evidence pipeline and overall gate are not complete.
- Incomplete reports must not be represented as complete evidence.

**Required-suite quality-gate conditions**
The gate should pass only when every required suite:
1. Uses an artifact whose integrity and source provenance were verified.
2. Completes execution.
3. Executes the required test category (not merely exits Maven successfully).
4. Has no blocking test failures.
5. Publishes mandatory evidence that can be collected for reporting.

**Knowledge checks completed**
| Scenario | User's answer | Correct principle |
|---|---|---|
| API passed, Selenium failed, Playwright passed, reports generated | B | Fail the gate because a required suite failed; retain reports |
| API and Selenium passed, required Playwright suite could not provision a runner | C | Fail the gate; report Playwright as NOT_STARTED and retain available evidence |
| Maven exits successfully but API integration-test flag was omitted and zero tests ran | C | Fail the gate; validate executed test count/category, not only process exit code |

**Project-specific test-selection reminder**
The API tests are gated by `Boolean.getBoolean("integration.tests")`; the intended Maven invocation includes `-Dintegration.tests=true`. The quality gate/result verifier must catch a missing flag or other condition that causes the required suite to execute zero tests.

**Next**
- [ ] Define the exact JSON schema and allowed status transitions for suite result records.
- [ ] Decide how to represent test failures versus infrastructure/prerequisite failures in the final gate summary.
- [ ] Map the contract to the existing workflow jobs and reporting scripts.
- [ ] Implement only after reviewing the proposed workflow changes; keep all work on `learning/ci-cd-quality-gates` and leave `master` untouched.


**Further result-contract refinement — incomplete execution**
- If a runner crashes after a suite has started, classify execution as `INCOMPLETE`, not `COMPLETED` or `NOT_STARTED`.
- Set the final test outcome to `UNKNOWN` unless the framework can establish a reliable final outcome from persisted results.
- Preserve the number of tests known to have executed and any partial result files.
- Record the infrastructure failure reason (for example, `RUNNER_CRASH`). The final test outcome can be unknown even when the infrastructure failure reason is known.
- A required suite with incomplete execution is blocking for the quality gate.

Illustrative record:
```json
{
  "suite": "SELENIUM",
  "executionStatus": "INCOMPLETE",
  "testOutcome": "UNKNOWN",
  "testsExecuted": 100,
  "failureReason": "RUNNER_CRASH"
}
```

Knowledge check: user correctly selected **C** for a Selenium suite that ran 100 tests before the runner crashed. Correct principle: retain partial evidence, classify the run as incomplete, and do not infer a final pass/fail test outcome.


## Session 4 — Final quality-gate design exercise and closeout

### Final scenario

- API: `COMPLETED / PASSED`, 120 tests executed, evidence uploaded.
- Selenium: `INCOMPLETE / UNKNOWN`, 100 tests executed, evidence uploaded; runner crashed.
- Playwright: `COMPLETED / PASSED`, 85 tests executed, evidence uploaded.
- All artifacts passed checksum and commit verification.
- Allure and Extent reports generated successfully from available results.

**Correct quality-gate decision: FAIL.**

The user selected **B** and correctly reasoned that a suite that does not complete requires investigation. Selenium is a required suite, so its `INCOMPLETE` execution blocks the gate. Partial evidence must be retained for diagnosis, but evidence upload and successful report generation do not turn an incomplete suite into a pass. The passing API and Playwright suites cannot compensate for the incomplete Selenium run.

### Session 4 closeout

Completed the final gate-design exercise. Key principle reinforced: the quality gate evaluates verified execution and test outcomes for every required suite; it must not infer success from partial results or successful reporting. Preserve partial evidence and the known infrastructure failure reason (`RUNNER_CRASH`) for investigation.

**Next session:** move from architecture/design into a careful implementation plan. Reinspect the current workflow and define job dependencies, artifact handoffs, suite-result contract, and gate behavior before making workflow changes.


## 11. Today's Lead SDET principles — one-line playbook (28 November 2026)

These are the concise principles and mental models from today's learning. Keep them as a quick revision sheet; the detailed explanations, examples and validation notes remain in the sections above.

### Lead SDET mindset

- **Senior SDET:** “Are all my tests running?” **Lead SDET:** “Are they running independently, reliably, quickly, securely, and do their results make the right release decision?”
- **Lead SDET →** Own the confidence to release, not just the number of tests written.
- **Lead SDET →** Design the system that makes failures visible, diagnosable and actionable.
- **Lead SDET →** Optimize for trustworthy feedback, not merely a green pipeline.
- **Lead SDET →** Treat test infrastructure, evidence and release decisions as engineering responsibilities.
- **Lead SDET →** Turn repeated manual effort into reliable automation and repeatable engineering practices.

### Pipeline architecture and execution

- **Build once; test the same build everywhere.**
- **One build artifact; independent test environments.**
- **A shared JAR is not a shared process; every isolated runner needs its own reachable application instance.**
- **Parallelize independent suites; preserve explicit dependencies where they matter.**
- **A Maven module is a code/build boundary; a CI job is an execution/isolation boundary.**
- **Parallel execution should reduce feedback time without reducing test independence.**
- **More workers do not automatically mean faster feedback; capacity and contention set the limit.**
- **Balance shards by execution time, not only by test count.**
- **Use unique test data, users, ports and resources for parallel workers.**
- **Concurrency controls coordinate workflow runs; they do not isolate test data.**
- **Use bounded readiness polling, not blind fixed sleeps.**
- **When startup fails, capture application logs before the evidence disappears.**

### Artifact integrity and provenance

- **A passing test against the wrong binary is not valid evidence.**
- **Checksum proves byte integrity; provenance proves where the artifact came from.**
- **Verify the downloaded artifact against the checksum created by the build job—not against a checksum generated beside the download.**
- **Trace every release: source commit → build → artifact → test results → deployment.**
- **Deploy the exact artifact that passed testing; do not rebuild at deployment time.**
- **Never silently replace a missing, stale or unverified artifact.**
- **A failed consumer download should fail that consumer; independent consumers can continue if their own artifacts verify.**
- **Treat build artifacts as immutable inputs to downstream jobs.**

### Test selection, execution and result contracts

- **A successful Maven exit code does not prove that the required tests ran.**
- **Validate executed test counts and categories; do not trust process exit codes alone.**
- **Execution status and test outcome are two different dimensions.**
- **COMPLETED does not mean PASSED; it means execution finished and the outcome can be evaluated.**
- **NOT_STARTED is not a pass; a required suite that never starts blocks the gate.**
- **SKIPPED is acceptable only when policy explicitly says the suite is not required for that run.**
- **INCOMPLETE means execution started but did not finish reliably.**
- **When a runner crashes, preserve known executed tests, mark the final outcome UNKNOWN when necessary, and record the infrastructure reason.**
- **A required suite must produce verifiable execution and evidence—not merely a successful job status.**
- **Make every suite speak the same machine-readable result contract.**

### Failure handling, retries and diagnostics

- **Let independent suites finish even when one suite fails.**
- **Fail the release gate when any required suite fails, cannot start or remains incomplete.**
- **Retry transient failures selectively; never use retries to hide product defects.**
- **A test that passes only on retry is still a flaky-test signal.**
- **Preserve first-attempt failures, retry outcomes, screenshots, traces and logs.**
- **A runner crash is an execution failure, even if the tests completed before the crash appear green.**
- **Do not turn unknown into passed.**
- **Investigate missing or partial evidence instead of assuming success.**

### Artifacts, reporting and quality gates

- **Independent jobs need explicit artifact handoffs; runner filesystems are not shared.**
- **Give each suite its own artifact identity and preserve its directory boundary.**
- **Never flatten result artifacts blindly; duplicate attachment names can overwrite evidence.**
- **Keep Allure result JSON and the attachments it references together.**
- **Generate diagnostic reports even when tests fail, whenever enough evidence exists.**
- **A successful report is not a successful test run.**
- **Report publication and application deployment are different decisions.**
- **Publish failure diagnostics; block an unsafe release.**
- **The quality gate must evaluate every required suite, not just the reporting job.**
- **A required suite that did not execute is a quality-gate failure, not a neutral result.**
- **Partial evidence helps investigation; it cannot certify completion.**
- **A green pipeline must mean verified, complete and successful required testing.**

### Project-specific reminders captured today

- API suite selection requires `-Dintegration.tests=true`.
- Selenium JUnit selection requires `-Dui.tests=true`.
- Playwright selection requires `-Dplaywright.tests=true -Dcucumber.filter.tags=@playwright`.
- API Allure results: `api-tests/target/allure-results`.
- Selenium and Playwright Allure results: `ui-tests/target/allure-results` in their separate job workspaces.
- Cucumber JSON: `ui-tests/target/cucumber-report.json`.
- Selenium failure evidence: `ui-tests/target/failure-artifacts`.
- Spring Boot executable packaging requires the configured `repackage` goal; the remote POM fix was verified.
- Current workflow is still sequential; no workflow refactor was made today.
- Preserve the existing trigger scope until explicitly reviewed; keep implementation on `learning/ci-cd-quality-gates`.

## 12. Today's learning — compact point-wise revision

1. GitHub Actions building blocks: workflows, triggers, jobs, runners, steps, artifacts, permissions, dependencies and concurrency.
2. Why a multi-job design helps: API, Selenium and Playwright can execute independently and in parallel.
3. Runner isolation: every job normally receives a fresh runner; localhost and local files do not cross job boundaries.
4. Pipeline shape: build/package → parallel suites → collect results → reporting → quality gate → report publication/release decision.
5. Build-once strategy: package one runnable application artifact, upload it, and let each suite download and start its own instance.
6. Artifact failure boundaries: central build failure blocks dependent suites; one consumer's download failure need not block other consumers.
7. Readiness: use bounded polling against a confirmed endpoint; capture logs and fail clearly on timeout.
8. Retry policy: retry only classified transient issues; preserve first-attempt evidence and report flaky tests.
9. Parallelism: understand workflow-, job-, worker- and shard-level parallelism; tune against real resource capacity.
10. Isolation: prevent collisions across test data, users, tenants, ports, browser sessions, databases and external service limits.
11. Concurrency: control overlapping runs deliberately; cancellation policy and test-data isolation are separate concerns.
12. Artifact safety: unique artifact names, separate download directories, no blind flattening, preserve Allure attachments.
13. Integrity: verify SHA-256 using the checksum generated by the build producer.
14. Provenance: verify source commit metadata as well as checksum; preserve the commit-to-deployment chain.
15. Exact deployment: deploy the same artifact that passed the required tests.
16. Maven understanding: distinguish module boundaries from CI job boundaries; Selenium and Playwright can run separately even though they share the UI module.
17. Suite selection: pass the correct Maven system properties and Cucumber tag filters; assert that the intended tests actually executed.
18. Reporting: preserve API, Selenium and Playwright evidence separately before aggregation; keep Cucumber JSON and failure artifacts.
19. Suite-result contract: record suite, commit, verification status, execution status, outcome, counts, evidence status and failure reason.
20. Status semantics: COMPLETED, NOT_STARTED, SKIPPED and INCOMPLETE describe execution; PASSED, FAILED and UNKNOWN describe outcome.
21. Gate policy: every required suite must have verified provenance, complete execution, actual test execution, no blocking failures and mandatory evidence.
22. Failure scenario: API and Playwright passed, Selenium runner crashed after 100 tests → Selenium INCOMPLETE/UNKNOWN, retain evidence, final gate FAIL.
23. Reporting scenario: Allure and Extent can succeed while the quality gate fails; reporting success never overrides test status.
24. Implementation status: executable Spring Boot JAR packaging fix verified remotely; workflow refactor remains pending.
25. Next step: inspect the current workflow and module commands, design the job/result/artifact contract, then implement incrementally on this learning branch.

**End-of-day takeaway:** A Lead SDET does not simply make tests run in CI. A Lead SDET engineers trustworthy evidence and a release decision that the team can safely rely on.
