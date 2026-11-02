# 0002 — MySQL with Flyway migrations

**Status:** Accepted

## Context

The brief asks for a relational database, suggesting SQLite. The application must be deployed and must compute salary statistics, including a median, over 10,000 rows.

## Options considered

| Option | Pros | Cons |
|---|---|---|
| SQLite | Zero setup; suggested by the brief | Free hosts have ephemeral file systems, so data is lost on redeploy; weaker concurrency |
| H2 locally, a server database in production | Nothing to install locally | Two SQL dialects; bugs can hide until production |
| PostgreSQL everywhere | `percentile_cont` gives the median directly | Not available on the development machine; one more install |
| **MySQL 8 everywhere** | Already installed and running locally; same engine in development and production; window functions; free managed hosting available | No built-in median function |

## Decision

Use **MySQL 8** in all environments. Manage the schema with **Flyway** versioned migrations; Hibernate validates the schema but never changes it.

The median salary is computed in SQL with window functions (`ROW_NUMBER()` and `COUNT()` over each group), so statistics still never require loading rows into the application.

## Consequences

- ✅ What is tested locally is what runs in production.
- ✅ Schema changes are explicit, reviewed and repeatable.
- ❌ The median query is more involved than PostgreSQL's `percentile_cont`; it is covered by persistence tests.
- ❌ Contributors need MySQL 8 installed locally (documented in the README).
