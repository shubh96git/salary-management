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
- **Developer judgement:** Reviewed scope and assumptions before committing.
