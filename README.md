# Quality Engineering Framework

A Java practice project demonstrating database access, JSON handling, API testing, and file operations.

## Database configuration

Database credentials are not stored in the repository. Configure them as environment variables before running database examples:

```text
SQL_DB_USERNAME=<dedicated database username>
SQL_DB_KEY=<your database password>
```

The JDBC URL defaults to `src/main/resources/config.properties` (loaded from the classpath, so the working directory does not matter):

```properties
sqlDbUrl=jdbc:mysql://localhost:3306/dbo
```

You can override the URL with the `SQL_DB_URL` environment variable. Do not put usernames or passwords in the JDBC URL, properties file, source code, test reports, or CI logs.

### Database security controls

- **Parameterized SQL:** both query methods use `PreparedStatement`. Bind all external values using `?` placeholders; never concatenate user-controlled values into SQL. Table and column identifiers cannot be bound, so select them only from a strict allow-list.
- **Read-only access:** HikariCP connections are configured as read-only. Use a dedicated MySQL account with only the required `SELECT` privileges for this utility; do not use an application administrator/root account.
- **Transport encryption:** localhost development URLs are allowed without TLS. For non-local MySQL hosts, the JDBC URL is automatically configured with `sslMode=VERIFY_IDENTITY`. The MySQL server certificate must chain to a CA trusted by the JVM. Do not weaken this to `REQUIRED`, `PREFERRED`, or `DISABLED`; install/configure the correct CA truststore instead.
- **Connection pool limits:** the pool has a bounded maximum size, connection/validation timeouts, idle timeout, and maximum connection lifetime.
- **Resource handling:** connections, statements, and result sets use try-with-resources and are closed reliably.
- **Error hygiene:** database errors do not log SQL text or bound parameter values. The thrown exception exposes only the SQLState in its message.

Example of safe parameter binding:

```java
dbUtils.executePreparedQuery(
    "SELECT EMP_ID, EMP_NAME FROM EMPLOYEE WHERE EMP_NAME = ? AND EMP_ID = ?",
    "John",
    1
);
```

For production, provision a dedicated database user with only the permissions required by the workload. Example for a read-only account (run as a database administrator and replace the placeholders):

```sql
CREATE USER 'app_readonly'@'your-application-host' IDENTIFIED BY 'use-a-strong-secret';
GRANT SELECT ON dbo.* TO 'app_readonly'@'your-application-host';
```

Store the password in a secret manager or protected CI secret, rotate it periodically, and restrict database network access to trusted application hosts. The local development URL is not a production TLS configuration.

## Authentication model

This project does not implement application user authentication, OAuth, JWTs, sessions, or access/refresh tokens. Database authentication is performed by MySQL using the database username and password supplied to HikariCP.

## Running

The project uses Maven. Ensure the configured MySQL database is running and the required environment variables are available to the Java process before running database examples or tests. Database integration tests skip when credentials are not configured; the JDBC security policy tests run without a live database.

## Java Multi-Module Test Automation Framework

This repository also contains a standalone Java automation framework and Service Desk demo application under `automation-parent/`. It covers JUnit 5/Mockito unit testing, REST Assured API testing, WireMock, JSON Schema, Selenium Page Objects, Playwright with Cucumber BDD, Allure and Extent reporting, and GitHub Actions CI.

**Start here:** [Automation Framework README](automation-parent/README.md) — complete setup guide, architecture, manual application startup, test execution commands, report generation, CI workflow and troubleshooting.

**Java coding practice:** [10-topic Java interview preparation guide](automation-parent/docs/JAVA_CODING_INTERVIEW_PREPARATION.md) — Strings/Arrays, HashMaps, Collections, Sets, Sorting, Two Pointers, Sliding Window, Stacks/Queues, Streams and Concurrency.

- [Live test dashboard](https://kkmahajan.github.io/quality-engineering-framework/)
- [Allure report](https://kkmahajan.github.io/quality-engineering-framework/allure/)
- [Extent report](https://kkmahajan.github.io/quality-engineering-framework/extent/)
- [Automation Framework CI runs](https://github.com/kkmahajan/quality-engineering-framework/actions/workflows/automation-framework-ci.yml)

The automation framework's CI workflow runs on pull requests and on pushes to `master` that change `automation-parent/**`.
