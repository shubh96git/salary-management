# 0003 — Single employee table, no lookup tables

**Status:** Accepted

## Context

Employees have a job title, department and country. These could be normalised into separate tables with foreign keys.

## Decision

Store job title, department and country code as **columns on the employee table**. Countries and their currencies are fixed reference data in code. Distinct values for UI filters come from `SELECT DISTINCT` queries.

## Consequences

- ✅ Simple schema, simple queries, simple seed script.
- ✅ Matches how HR data arrives from Excel today.
- ❌ No central place to rename a job title or department; typos create new values. Mitigated by offering existing values in the UI.
- 🔁 Revisit if job titles or departments gain their own attributes (e.g. salary bands).
