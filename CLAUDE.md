# Working agreement for the AI assistant

This file instructs the AI coding assistant (Claude Code) used to build this project. It is committed so reviewers can see how AI was directed.

## Context

- Read `docs/requirements.md` and `docs/architecture.md` before making changes. Stay inside the documented scope; propose scope changes instead of making them.
- Stack: Java 21, Spring Boot 4, Maven wrapper, MySQL 8 + Flyway, Angular + Angular Material.

## How to work

- **Test first.** For business logic, write a failing test, then the minimum code to pass, then refactor.
- **Small steps.** One behaviour per change.
- **TDD is visible in history.** Commit the failing test first (`test:`, the only kind of commit allowed to fail, and only on the new test), then the code that makes it pass (`feat:`/`fix:`), then any `refactor:`. Failing tests compile against a minimal stub, so they fail on an assertion, not a compile error. Every non-`test:` commit builds and passes all tests.
- **Explain, don't just generate.** State the reasoning for non-obvious design choices; record significant ones as an ADR in `docs/decisions/`.
- **Ask when unsure.** Do not invent requirements; flag ambiguity to the developer.

## Code standards

- Package by feature (`employee`, `insights`, `seed`, `common`), layered inside each feature.
- Controllers contain no business logic. Entities are never returned from the API; use DTOs (Java records).
- Money is `BigDecimal` / `numeric(12,2)`, never `double`.
- Constructor injection only. No field injection.
- Clear names over comments; comment only the *why*.
- No dead code, no commented-out code, no unused dependencies.

## Tests

- Fast and deterministic: no real clock, no unseeded randomness, no test-order dependencies.
- Name tests by behaviour, e.g. `rejectsNegativeSalary`.
- One reason to fail per test; use AssertJ for readable assertions.

## Commits

- Conventional Commits: `feat:`, `test:`, `refactor:`, `fix:`, `docs:`, `build:`, `chore:`.
- Imperative, lowercase summary under 72 characters, e.g. `feat: add salary stats per country`.
- Body explains *why* when it is not obvious.
- The developer reviews every change before it is committed.
