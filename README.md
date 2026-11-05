# Salary Management

A web application for ACME's HR Manager to manage salary data for 10,000 employees across multiple countries and understand how the organisation pays people.

- **Live application:** <https://salary-management-production-07aa.up.railway.app> (Railway; Docker image plus managed MySQL, seeded with 10,000 employees)
- **Video demo:** <https://www.loom.com/share/ea70e0551c2c4833b825f322b5af8fd8>

## What it does

- **Employees:** find anyone among 10,000 employees by name or email; filter by country, job title and department; sort and page; add, edit and delete with validation. Salaries are shown in each employee's local currency.
- **Insights:** headcount, median, average, range and total payroll per country, then per job title or department within a country, so the HR Manager can answer "how does the organisation pay people?" without spreadsheets.

What is in scope, what was deliberately left out and why, is in the [requirements document](docs/requirements.md), written before any code.

## How it was built

- **Planning first:** requirements, architecture, decision records and the delivery plan were committed before the first line of code.
- **Test-driven, visibly:** each behaviour was added as a failing-test commit (`test:`) followed by the commit that makes it pass (`feat:`/`fix:`), with `refactor:` commits where the design improved. Only `test:` commits contain failing tests.
- **AI-assisted, developer-directed:** built with Claude Code under the instructions in [CLAUDE.md](CLAUDE.md). [docs/ai-usage.md](docs/ai-usage.md) records what AI was asked, and where its output was checked or corrected.

| Quality measure | Backend | Frontend |
|---|---|---|
| Tests | 69 (JUnit, Spring test slices, MySQL) | 36 (Vitest, Material component harnesses) |
| Line coverage | 95% | 96% |
| Branch coverage | 83% | 83% |
| API response time over 10,000 employees | 20–150 ms ([details](docs/performance.md)) | |

## Documentation

| Document | What it covers |
|---|---|
| [Requirements](docs/requirements.md) | Goal, scope, what is left out and why, assumptions |
| [Architecture](docs/architecture.md) | System design, data model, API, performance considerations |
| [Decisions](docs/decisions/) | Architecture decision records and trade-offs |
| [Delivery plan](docs/plan.md) | Milestones, way of working, testing strategy |
| [Performance](docs/performance.md) | Measurements and speed-related decisions |
| [AI usage](docs/ai-usage.md) | How AI tools were used and checked |
| [CLAUDE.md](CLAUDE.md) | Instructions given to the AI assistant |

## Tech stack

Java 21 · Spring Boot 4 · MySQL 8 · Flyway · Angular 22 · Angular Material

## Running locally

### Prerequisites

- JDK 21 (`JAVA_HOME` pointing to it)
- MySQL 8 running on `localhost:3306`
- Node.js 22.22.3 or later (for the frontend)

Maven does not need to be installed; the project uses the Maven wrapper.

### Database

Run once as the MySQL `root` user:

```sql
CREATE DATABASE salary_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE salary_management_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'salary_app'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON salary_management.* TO 'salary_app'@'localhost';
GRANT ALL PRIVILEGES ON salary_management_test.* TO 'salary_app'@'localhost';
```

The schema itself is created by Flyway when the application starts. Tests use the separate `salary_management_test` database (override with `TEST_DB_URL`), so they never see development or seed data.

### Configuration

The backend reads its settings from environment variables:

| Variable | Default | Required |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/salary_management` | No |
| `DB_USERNAME` | `salary_app` | No |
| `DB_PASSWORD` | — | Yes |
| `SEED_ENABLED` | `false` | No |

### Seed data

Start the application with `SEED_ENABLED=true` to load **10,000 employees** into an empty database:

```bash
cd backend
SEED_ENABLED=true ./mvnw spring-boot:run
```

- **Deterministic:** the same 10,000 employees every time (fixed random seed), so demos and screenshots are reproducible.
- **Safe to repeat:** seeding is skipped when any employee already exists.
- **Realistic:** names typical of each of 8 countries, 13 job titles across 9 departments, salaries in local currency from per-role bands scaled to each market, and hire dates from 2012 to 2025.

To reseed from scratch, empty the table (`TRUNCATE TABLE employee;`) and start again with seeding enabled.

### Backend

```bash
cd backend
./mvnw spring-boot:run    # start on http://localhost:8080
./mvnw test               # run the tests; coverage report in target/site/jacoco/index.html
```

Health check: <http://localhost:8080/actuator/health>

### Frontend

With the backend running on port 8080:

```bash
cd frontend
npm ci                    # install the exact dependency versions from package-lock.json
npm start                 # start on http://localhost:4200; /api is proxied to the backend
npm test -- --watch=false # run the tests once
npm run test:coverage     # run the tests with a coverage report in coverage/
```

## Deployment

The [Dockerfile](Dockerfile) builds one image: it compiles the Angular app, bundles it into the Spring Boot jar as static content, and runs it on a Java 21 runtime. Any host that builds Dockerfiles and provides MySQL 8 can run it with these variables:

| Variable | Value |
|---|---|
| `DB_URL` | `jdbc:mysql://<host>:<port>/<database>` |
| `DB_USERNAME`, `DB_PASSWORD` | Database credentials |
| `SEED_ENABLED` | `true` to load 10,000 employees on first start |
| `PORT` | Set by most hosts; defaults to 8080 |

Flyway creates the schema on startup.

## Using the application

- **Employees** (`/employees`): search by name or email, filter by country, job title and department, sort any column, and page through results. Add, edit and delete employees; deleting asks for confirmation. Salaries are shown in each employee's local currency.
- **Insights** (`/insights`): headcount, median, average, range and total payroll per country. Select a country (`/insights/IN`) to see the same figures per job title or department, with a bar showing each group's pay range and median.

