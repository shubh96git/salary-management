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
