# AI Usage Log

How AI tools were used to build this project, what they were asked, and where their output was checked or corrected.

**Tool:** Claude Code (agentic coding assistant) in VS Code.
**Standing instructions:** [CLAUDE.md](../CLAUDE.md).

## Principles

- AI accelerates; the developer decides. Every change is reviewed before it is committed.
- AI output is verified by tests, by running the application, and by reading the code.
- Prompts ask for reasoning and trade-offs, not just code.

## Log

### 1. Understanding the assessment

- **Prompt (summary):** Shared the invitation email and asked for a plan, noting that the assessment values incremental development and commit hygiene.
- **AI output:** Identified what reviewers will look for (clarifying questions, TDD visible in history, AI workflow, documented decisions) and proposed a phased approach.
- **Developer judgement:** Accepted the phased approach; shared the full assessment document before any design work.

### 2. Breaking down the requirements

- **Prompt (summary):** Shared the assessment document and asked for what needs to be done.
- **AI output:** Extracted the deliverables, split the product into "manage salaries" and "answer pay questions", proposed scope exclusions, clarification questions and a commit sequence.
- **Developer judgement:** Chose Java 21 + Spring Boot; set up the toolchain (JDK 21, `JAVA_HOME`, Node 22, Git identity) before starting.

### 3. Planning documents

- **Prompt (summary):** Initialise the repository and draft the requirements document plus all planning artifacts before any code.
- **AI output:** Requirements, architecture (with diagrams), ADRs, delivery plan and this working agreement.
- **Developer judgement:** Reviewed scope and assumptions before committing. Reworded assumptions as justified decisions rather than open questions.

### 4. Database choice

- **Prompt (summary):** Asked whether MySQL, already installed locally, could replace the planned PostgreSQL.
- **AI output:** Confirmed MySQL 8 fits, named the trade-off (no built-in median, so window functions are needed) and updated the decision record.
- **Developer judgement:** Chose MySQL to keep development and production on the same engine without another install.

### 5. Backend skeleton

- **Prompt (summary):** Set up the Spring Boot project.
- **AI output:** Checked Spring Initializr before generating and found only Spring Boot 4.x offered (3.x is out of open-source support), so used 4.1.2 instead of the planned 3.x. Removed generator noise, moved database settings to environment variables, and verified the startup test against local MySQL before committing.
- **Developer judgement:** Accepted Spring Boot 4; kept credentials out of the repository.

### 6. Employee management API (test-driven)

- **Prompt (summary):** Build employee create, read, update, delete and search test-first, committing every red, green and refactor step.
- **AI output:** Small red → green → refactor cycles for the domain, service, search and REST layers, with each red commit failing on an assertion rather than a compile error.
- **Where AI output was checked or corrected:**
  - A first draft used a backslash as the SQL `LIKE` escape character. MySQL also treats backslashes as escapes inside string literals, so `!` was used instead, with a test proving `%` is matched literally.
  - Spring Boot 4 moved several test annotations to new packages. Their locations were looked up in the downloaded libraries rather than guessed from Boot 3 examples.
  - One test run reported "unresolved compilation problems" because the IDE's background compiler rewrote `target/` mid-build. Diagnosed from the error format and fixed by always running clean builds.
  - Unit tests passed, but a manual end-to-end run against MySQL showed an updated salary returned as `61000` instead of `61000.00`. Fixed in the domain with a new test.
- **Developer judgement:** Chose red/green commits so test-first is visible in the history; asked for every step to be committed and nothing pushed until reviewed.

### 7. Seed data

- **Prompt (summary):** Seed 10,000 employees.
- **AI output:** A pure, deterministic generator (country-typical names, job-to-department mapping, salary bands scaled per market) tested for size, determinism, unique emails, valid data and pay seniority; plus an idempotent JDBC batch seeder tested against MySQL.
- **Where AI output was checked or corrected:** Seeding took 8–10 s, which looked too slow. Instead of tuning blindly, the time was measured: a raw SQL insert of the same 10,000 rows took 7.3 s, proving the local database, not the code, was the limit. Recorded in [performance.md](performance.md).
- **Developer judgement:** Seeding stays off by default and is enabled per environment.

### 8. Salary insights

- **Prompt (summary):** Answer "how does the org pay people?" with statistics per country, job title and department.
- **AI output:** One SQL template with window functions for the median, tested against MySQL with small hand-checkable data (even and odd group sizes); REST endpoints and filter options built test-first.
- **Where AI output was checked or corrected:**
  - Adding seed data to the development database broke 8 existing tests, which had silently shared that database. Tests were moved to a dedicated test database via a shared `@DatabaseTest` annotation.
  - Medians were checked by hand in the tests (250 for 100/200/300/1000; 70 for 50/70/90) and against the seeded data, where medians sit below averages as expected for salaries.
- **Developer judgement:** No pass-through service for read-only insights; endpoints measured at 20–150 ms over 10,000 employees.
