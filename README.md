# IntelliJJavaSQL

A Java practice project demonstrating database access, JSON handling, API testing, and file operations.

## Database configuration

Database credentials are not stored in the repository. Configure them as environment variables before running database examples:

```text
SQL_DB_USERNAME=<your database username>
SQL_DB_KEY=<your database password>
```

The JDBC URL is configured in `src/test/resources/config.properties`:

```properties
sqlDbUrl=jdbc:mysql://localhost:3306/dbo
```

For a remote or production database, configure MySQL transport security appropriately and do not commit credentials to source control.

## Database access design

`DatabaseUtils` owns a HikariCP connection pool. It reads the JDBC URL from the properties file and credentials from environment variables. Connections, statements, prepared statements, and result sets use try-with-resources so they are returned/closed reliably.

Use `executeQuery` only for SQL with no external parameters. For parameterized SQL, use `executePreparedQuery` and `?` placeholders:

```java
dbUtils.executePreparedQuery(
    "SELECT * FROM EMPLOYEE WHERE EMP_NAME = ? AND EMP_ID = ?",
    "John",
    1
);
```

Do not construct SQL by concatenating or replacing user-controlled values.

## Authentication model

This project does not implement user authentication, OAuth, JWTs, sessions, or access/refresh tokens. Authentication is performed by MySQL using the database username and password supplied to the JDBC driver through HikariCP.

## Running

The project uses Maven. Ensure the configured MySQL database is running and the required environment variables are available to the Java process before running database examples or tests.
