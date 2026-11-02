# Delivery Plan

How the solution will be built, in order. Each milestone ends in a working, tested state.

## Milestones

| # | Milestone | Outcome |
|---|---|---|
| 0 | **Planning** | Requirements, architecture, decisions, plan and AI working agreement committed before any code |
| 1 | **Backend skeleton** | Spring Boot app builds, starts and connects to MySQL; Flyway creates the schema |
| 2 | **Employee management API** | CRUD, search, filters, pagination, validation, consistent errors |
| 3 | **Seed data** | Deterministic generator and runner create 10,000 employees |
| 4 | **Salary insights API** | Per-country, per-job-title and per-department statistics |
| 5 | **Frontend skeleton** | Angular app with Material, routing and layout, served by Spring Boot |
| 6 | **Employees UI** | List, search, filter, add, edit, delete |
| 7 | **Insights UI** | Country summary and drill-down by job title and department |
| 8 | **Deployment** | Docker image deployed with managed MySQL; README run instructions |
| 9 | **Wrap-up** | Performance notes, AI usage log, demo video, README polish |

## Way of working

- **Test-driven development** for business logic: write a failing test, make it pass, then refactor. Each step is its own commit, so the red → green → refactor cycle is visible in the history; only `test:` commits may contain a failing test.
- **Small commits** that each build and pass tests, using [Conventional Commits](https://www.conventionalcommits.org/):

  | Prefix | Use |
  |---|---|
  | `feat:` | New behaviour |
  | `test:` | Tests added ahead of or alongside behaviour |
  | `refactor:` | Structure change with no behaviour change |
  | `fix:` | Bug fix |
  | `docs:` | Documentation only |
  | `build:` / `chore:` | Build, tooling, dependencies |

- **Docs evolve with the code.** When a decision changes, its ADR is updated in the same commit.

## Testing strategy

| Level | What | Tools |
|---|---|---|
| Unit | Services, validation rules, currency derivation, seed generator | JUnit 5, AssertJ, Mockito — no Spring context, milliseconds per test |
| Web slice | Controllers: request mapping, validation errors, response shape | `@WebMvcTest` with mocked services |
| Persistence | Search filters and aggregate SQL against a real database | `@DataJpaTest` with MySQL |
| Frontend | Components and services with HTTP mocked | Angular's default test runner |

Tests are deterministic: no reliance on current time or random data without a fixed seed, and no ordering dependencies.
