# SDET Framework

A BDD API test automation framework for validating REST services and database state using plain-language scenarios. Tests are written in Gherkin feature files and executed with Cucumber, while HTTP calls and assertions are handled by reusable Java components.

## Project Overview

This framework enables QA engineers and developers to automate API tests without duplicating boilerplate for HTTP clients, JSON payloads, or database checks. Scenarios describe **what** to test in readable steps; step definitions connect those steps to RestAssured API calls and JDBC database queries.

Key capabilities:

- Send GET, POST, PUT, and DELETE requests against a configurable base URL
- Assert HTTP status codes and response body content
- Query PostgreSQL and validate result data
- Load request payloads from JSON files
- Generate Cucumber HTML and JSON reports after each run

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Core language and runtime |
| RestAssured | REST API client |
| Cucumber | BDD scenarios and step matching |
| JUnit 4 | Test runner integration |
| PostgreSQL | Database validation |
| Docker | Local database via Docker Compose |
| Maven | Build and test execution |
| Jackson | JSON parsing and manipulation |
| GitHub Actions | Continuous integration |

## Project Structure

```
sdet-framework/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
│   └── test/
│       ├── java/
│       │   └── framework/
│       │       ├── TestRunner.java
│       │       ├── client/
│       │       │   └── BaseClient.java
│       │       ├── database/
│       │       │   └── DatabaseHelper.java
│       │       ├── helpers/
│       │       │   └── JsonHelper.java
│       │       └── steps/
│       │           └── StepDefinitions.java
│       └── resources/
│           ├── config.properties
│           ├── features/
│           │   └── sample_api.feature
│           └── payloads/
├── docker-compose.yml
├── pom.xml
└── README.md
```

| Path | Description |
|------|-------------|
| `features/` | Gherkin `.feature` files written by QA |
| `payloads/` | JSON request bodies referenced in scenarios |
| `config.properties` | API base URL and database connection settings |
| `framework/steps/` | Step definitions that map Gherkin to code |
| `framework/client/` | HTTP client wrapper around RestAssured |
| `framework/database/` | JDBC helper for PostgreSQL |
| `framework/helpers/` | JSON utilities for payloads |

## How to Run Tests

**Prerequisites:** Java 17, Maven 3.8+

For database scenarios, start PostgreSQL locally:

```bash
docker compose up -d
```

Run all Cucumber scenarios:

```bash
mvn test
```

Reports are generated under:

- `target/cucumber-reports/cucumber.html`
- `target/cucumber-reports/cucumber.json`

## How to Write a New Test

QA can add tests by creating a `.feature` file under `src/test/resources/features/`. No Java coding is required—only the predefined steps below.

**Example:** create `src/test/resources/features/get_user.feature`

```gherkin
Feature: User API

  Scenario: Retrieve a user by ID
    When I send GET request to "/users/1"
    Then status code should be 200
    And response should contain "Leanne Graham"
```

**Available steps**

| Step | Example |
|------|---------|
| Send GET | `When I send GET request to "/users/1"` |
| Send POST | `When I send POST request to "/users" with payload "create-user.json"` |
| Send PUT | `When I send PUT request to "/users/1" with payload "update-user.json"` |
| Send DELETE | `When I send DELETE request to "/users/1"` |
| Assert status | `Then status code should be 200` |
| Assert response | `And response should contain "expected text"` |
| Query database | `When I query the database with "SELECT name FROM users WHERE id = 1"` |
| Assert database | `Then the result should contain "John Doe"` |

Place JSON payload files in `src/test/resources/payloads/` and reference them by filename in POST and PUT steps. Update `config.properties` if the API base URL or database credentials change.

See `sample_api.feature` for a working reference.

## CI/CD

GitHub Actions runs automatically on every **push** and **pull request** to the `main` branch.

The workflow (`.github/workflows/ci.yml`):

- Runs on Ubuntu with Java 17
- Starts a PostgreSQL service container for database tests
- Executes `mvn test`
- Publishes Surefire test results to the workflow summary
- Uploads Cucumber reports as downloadable artifacts

No manual steps are required after pushing to `main`—check the **Actions** tab in GitHub for build status and reports.
