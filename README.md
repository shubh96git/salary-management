# Salary Management

A web application for ACME's HR Manager to manage salary data for 10,000 employees across multiple countries and understand how the organisation pays people.

> **Status:** planning complete; implementation in progress.

## Documentation

| Document | What it covers |
|---|---|
| [Requirements](docs/requirements.md) | Goal, scope, what is left out and why, assumptions |
| [Architecture](docs/architecture.md) | System design, data model, API, performance considerations |
| [Decisions](docs/decisions/) | Architecture decision records and trade-offs |
| [Delivery plan](docs/plan.md) | Milestones, way of working, testing strategy |
| [AI usage](docs/ai-usage.md) | How AI tools were used and checked |
| [CLAUDE.md](CLAUDE.md) | Instructions given to the AI assistant |

## Tech stack

Java 21 · Spring Boot 4 · MySQL 8 · Flyway · Angular · Angular Material

## Running locally

### Prerequisites

- JDK 21 (`JAVA_HOME` pointing to it)
- MySQL 8 running on `localhost:3306`

Maven does not need to be installed; the project uses the Maven wrapper.

### Database

Run once as the MySQL `root` user:

```sql
CREATE DATABASE salary_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'salary_app'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON salary_management.* TO 'salary_app'@'localhost';
```

The schema itself is created by Flyway when the application starts.

### Configuration

The backend reads its database settings from environment variables:

| Variable | Default | Required |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/salary_management` | No |
| `DB_USERNAME` | `salary_app` | No |
| `DB_PASSWORD` | — | Yes |

### Backend

```bash
cd backend
./mvnw spring-boot:run    # start on http://localhost:8080
./mvnw test               # run the tests
```

Health check: <http://localhost:8080/actuator/health>
