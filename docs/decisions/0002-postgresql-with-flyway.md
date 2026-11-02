# 0002 — PostgreSQL with Flyway migrations

**Status:** Proposed (pending local PostgreSQL setup)

## Context

The brief asks for a relational database, suggesting SQLite. The application must be deployed and must compute salary statistics, including a median, over 10,000 rows.

## Options considered

| Option | Pros | Cons |
|---|---|---|
| SQLite | Zero setup; suggested by the brief | Free hosts have ephemeral file systems, so data is lost on redeploy; weaker concurrency |
| H2 locally, PostgreSQL in production | Nothing to install locally | Two SQL dialects; bugs can hide until production |
| **PostgreSQL everywhere** | Same engine in development and production; `percentile_cont` for median; free managed hosting widely available | Requires a local install |

## Decision

Use **PostgreSQL** in all environments. Manage the schema with **Flyway** versioned migrations; Hibernate validates the schema but never changes it.

## Consequences

- ✅ What is tested locally is what runs in production.
- ✅ Schema changes are explicit, reviewed and repeatable.
- ❌ Contributors need PostgreSQL installed locally (documented in the README).
